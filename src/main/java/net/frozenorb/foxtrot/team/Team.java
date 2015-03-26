package net.frozenorb.foxtrot.team;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.maps.KillsMap;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.serialization.LocationSerializer;
import net.frozenorb.qlib.util.TimeUtils;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Team {

    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
    public static final String GRAY_LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);

    public static final ChatColor ALLY_COLOR = ChatColor.BLUE;
    public static final ChatColor TRADING_COLOR = ChatColor.LIGHT_PURPLE;

    // Configurable values //

    public static final int MAX_TEAM_SIZE = 30;
    public static final int MAX_CLAIMS = 2;
    public static final int MAX_ALLIES = 0;
    public static final long DTR_REGEN_TIME = TimeUnit.MINUTES.toMillis(60);
    public static final long RAIDABLE_REGEN_TIME = TimeUnit.MINUTES.toMillis(60);

    // End configurable values //

    @Getter @Setter private ObjectId uniqueId;
    @Getter private String name;

    @Getter private boolean needsSave = false;
    @Getter private boolean loading = false;

    @Getter private Location HQ;
    @Getter private double balance;
    @Getter private double DTR;
    @Getter private long DTRCooldown;
    @Getter private List<Claim> claims = new ArrayList<>();
    @Getter private List<Subclaim> subclaims = new ArrayList<>();
    @Getter private UUID owner = null;
    @Getter private Set<UUID> members = new HashSet<>();
    @Getter private Set<UUID> captains = new HashSet<>();
    @Getter private Set<UUID> invitations = new HashSet<>();
    @Getter private Set<ObjectId> allies = new HashSet<>();
    @Getter private Set<ObjectId> requestedAllies = new HashSet<>();
    @Getter private String announcement;

    public Team(String name) {
        this.name = name;
    }

    public void setDTR(double newDTR) {
        if (DTR == newDTR) {
            return;
        }

        if (DTR <= 0 && newDTR > 0) {
            TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Team no longer raidable.");
        }

        if (!isLoading()) {
            Foxtrot.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + " --> " + newDTR);
        }

        this.DTR = newDTR;
        flagForSave();
    }

    public void setName(String name) {
        this.name = name;
        flagForSave();
    }

    public String getName(Player player) {
        if (owner == null) {
            if (hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                switch (player.getWorld().getEnvironment()) {
                    case NETHER:
                        return (ChatColor.GREEN + "Nether Spawn");
                    case THE_END:
                        return (ChatColor.GREEN + "The End Safezone");
                }

                return (ChatColor.GREEN + "Spawn");
            } else if (hasDTRBitmask(DTRBitmask.KOTH)) {
                return (ChatColor.AQUA + getName() + ChatColor.GOLD + " KOTH");
            } else if (hasDTRBitmask(DTRBitmask.CITADEL)) {
                return (ChatColor.DARK_PURPLE + "Citadel");
            }
        }

        if (isMember(player.getUniqueId())) {
            return (ChatColor.GREEN + getName());
        } else if (isAlly(player.getUniqueId())) {
            return (Team.ALLY_COLOR + getName());
        } else {
            return (ChatColor.RED + getName());
        }
    }

    public void addMember(UUID member) {
        members.add(member);
        TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Member Added: " + UUIDUtils.formatPretty(member));
        pushToMongoLog(new BasicDBObject("Type", "MemberAdded").append("Member", member.toString()));
        flagForSave();
    }

    public void addCaptain(UUID captain) {
        captains.add(captain);
        TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Captain Added: " + UUIDUtils.formatPretty(captain));
        pushToMongoLog(new BasicDBObject("Type", "CaptainAdded").append("Captain", captain.toString()));
        flagForSave();
    }

    public void setBalance(double balance) {
        this.balance = balance;
        flagForSave();
    }

    public void setDTRCooldown(long dtrCooldown) {
        this.DTRCooldown = dtrCooldown;
        flagForSave();
    }

    public void removeCaptain(UUID captain) {
        captains.remove(captain);
        TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Captain Removed: " + UUIDUtils.formatPretty(captain));
        pushToMongoLog(new BasicDBObject("Type", "CaptainRemoved").append("Captain", captain.toString()));
        flagForSave();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;

        if (owner != null) {
            members.add(owner);
        }

        TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Owner Changed: " + UUIDUtils.formatPretty(owner));
        pushToMongoLog(new BasicDBObject("Type", "OwnerChanged").append("NewOwner", owner == null ? "null" : owner.toString()));
        flagForSave();
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
        TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Announcement Changed: " + announcement);
        flagForSave();
    }

    public void setHQ(Location hq) {
        String oldHQ = this.HQ == null ? "None" : (getHQ().getBlockX() + ", " + getHQ().getBlockY() + ", " + getHQ().getBlockZ());
        String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
        this.HQ = hq;
        TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "HQ Changed: [" + oldHQ + "] -> [" + newHQ + "]");
        flagForSave();
    }

    public void disband() {
        try {
            if (owner != null) {
                Basic.get().getEconomyManager().depositPlayer(UUIDUtils.name(owner), balance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ObjectId allyId : getAllies()) {
            Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                ally.getAllies().remove(getUniqueId());
            }
        }

        Foxtrot.getInstance().getTeamHandler().removeTeam(this);
        LandBoard.getInstance().clear(this);

        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runRedisCommand(redis -> {
                    redis.del("fox_teams." + name.toLowerCase());
                    return (null);
                });

                DBCollection teamsCollection = Foxtrot.getInstance().getMongoPool().getDB("HCTeams").getCollection("Teams");
                teamsCollection.remove(getJSONIdentifier());
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());

        needsSave = false;
    }

    public void rename(String newName) {
        final String oldName = name;

        Foxtrot.getInstance().getTeamHandler().removeTeam(this);

        this.name = newName;

        Foxtrot.getInstance().getTeamHandler().setupTeam(this);

        qLib.getInstance().runRedisCommand(redis -> {
            redis.del("fox_teams." + oldName.toLowerCase());
            return (null);
        });

        // We don't need to do anything here as all we're doing is changing the name, not the Unique ID (which is what Mongo uses)
        // therefore, Mongo will be notified of this once the 'flagForSave()' down below gets processed.

        for (Claim claim : getClaims()) {
            claim.setName(claim.getName().replaceAll(oldName, newName));
        }

        flagForSave();
    }

    public void flagForSave() {
        needsSave = true;
    }

    public boolean isOwner(UUID check) {
        return (check.equals(owner));
    }

    public boolean isMember(UUID check) {
        for (UUID member : members) {
            if (check.equals(member)) {
                return (true);
            }
        }

        return (false);
    }

    public boolean isCaptain(UUID check) {
        for (UUID captain : captains) {
            if (check.equals(captain)) {
                return (true);
            }
        }

        return (false);
    }

    public boolean isAlly(UUID check) {
        Team checkTeam = Foxtrot.getInstance().getTeamHandler().getTeam(check);
        return (checkTeam != null && isAlly(checkTeam));
    }

    public boolean isAlly(Team team) {
        return (getAllies().contains(team.getUniqueId()));
    }

    public boolean ownsLocation(Location location) {
        return (LandBoard.getInstance().getTeam(location) == this);
    }

    public boolean ownsClaim(Claim claim) {
        return (claims.contains(claim));
    }

    public boolean removeMember(UUID member) {
        members.remove(member);
        captains.remove(member);

        // If the owner leaves (somehow)
        if (isOwner(member)) {
            Iterator<UUID> membersIterator = members.iterator();
            this.owner = membersIterator.hasNext() ? membersIterator.next() : null;
        }

        try {
            for (Subclaim subclaim : subclaims) {
                if (subclaim.isMember(member)) {
                    subclaim.removeMember(member);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DTR > getMaxDTR()) {
            DTR = getMaxDTR();
        }

        TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Member Removed: " + UUIDUtils.formatPretty(member));
        pushToMongoLog(new BasicDBObject("Type", "MemberRemoved").append("Member", member.toString()));
        flagForSave();
        return (owner == null || members.size() == 0);
    }

    public boolean hasDTRBitmask(DTRBitmask bitmaskType) {
        if (getOwner() != null) {
            return (false);
        }

        int dtrInt = (int) DTR;
        return (((dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask()));
    }

    public int getOnlineMemberAmount() {
        int amt = 0;

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
                amt++;
            }
        }

        return (amt);
    }

    public Collection<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
                players.add(exactPlayer);
            }
        }

        return (players);
    }

    public Collection<UUID> getOfflineMembers() {
        List<UUID> players = new ArrayList<>();

        for (UUID member : getMembers()) {
            Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);

            if (exactPlayer == null || exactPlayer.hasMetadata("invisible")) {
                players.add(member);
            }
        }

        return (players);
    }

    public Subclaim getSubclaim(String name) {
        for (Subclaim subclaim : subclaims) {
            if (subclaim.getName().equalsIgnoreCase(name)) {
                return (subclaim);
            }
        }

        return (null);
    }

    public Subclaim getSubclaim(Location location) {
        for (Subclaim subclaim : subclaims) {
            if (new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2()).contains(location)) {
                return (subclaim);
            }
        }

        return (null);
    }

    public int getSize() {
        return (getMembers().size());
    }

    public boolean isRaidable() {
        return (DTR <= 0);
    }

    public void playerDeath(String playerName, double dtrLoss) {
        double newDTR = Math.max(DTR - dtrLoss, -.99);
        TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Member Death: " + playerName + " [DTR Loss: " + dtrLoss + ", Old DTR: " + DTR + ", New DTR: " + newDTR + "]");

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Member Death: " + ChatColor.WHITE + playerName);
                player.sendMessage(ChatColor.RED + "DTR: " + ChatColor.WHITE + DTR_FORMAT.format(newDTR));
            }
        }

        Foxtrot.getInstance().getLogger().info("[TeamDeath] " + name + " > " + "Player death: [" + playerName + "]");
        setDTR(newDTR);

        if (isRaidable()) {
            TeamActionTracker.logActionAsync(this, TeamActionType.GENERAL, "Team now raidable.");
            DTRCooldown = System.currentTimeMillis() + RAIDABLE_REGEN_TIME;
        } else {
            DTRCooldown = System.currentTimeMillis() + DTR_REGEN_TIME;
        }

        DTRHandler.setCooldown(this);
    }

    public double getDTRIncrement() {
        return (getDTRIncrement(getOnlineMemberAmount()));
    }

    public double getDTRIncrement(int playersOnline) {
        double dtrPerHour = DTRHandler.getBaseDTRIncrement(getSize()) * playersOnline;
        return (dtrPerHour / 60);
    }

    public double getMaxDTR() {
        return (DTRHandler.getMaxDTR(getSize()));
    }

    public void load(String str) {
        loading = true;
        String[] lines = str.split("\n");
        //System.out.println(str);

        for (String line : lines) {
            String identifier = line.substring(0, line.indexOf(':'));
            String[] lineParts = line.substring(line.indexOf(':') + 1).split(",");

            if (identifier.equalsIgnoreCase("Owner")) {
                if (!lineParts[0].equals("null")) {
                    setOwner(UUID.fromString(lineParts[0].trim()));
                }
            } else if (identifier.equalsIgnoreCase("UUID")) {
                uniqueId = new ObjectId(lineParts[0].trim());
            } else if (identifier.equalsIgnoreCase("Members")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addMember(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Captains")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        addCaptain(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Invited")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        getInvitations().add(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("HQ")) {
                setHQ(parseLocation(lineParts));
            } else if (identifier.equalsIgnoreCase("DTR")) {
                setDTR(Double.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Balance")) {
                setBalance(Double.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DTRCooldown")) {
                setDTRCooldown(Long.parseLong(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FriendlyName")) {
                setName(lineParts[0]);
            } else if (identifier.equalsIgnoreCase("Claims")) {
                for (String claim : lineParts) {
                    claim = claim.replace("[", "").replace("]", "");

                    if (claim.contains(":")) {
                        String[] split = claim.split(":");

                        int x1 = Integer.parseInt(split[0].trim());
                        int y1 = Integer.parseInt(split[1].trim());
                        int z1 = Integer.parseInt(split[2].trim());
                        int x2 = Integer.parseInt(split[3].trim());
                        int y2 = Integer.parseInt(split[4].trim());
                        int z2 = Integer.parseInt(split[5].trim());
                        String name = split[6].trim();
                        String world = split[7].trim();

                        Claim claimObj = new Claim(world, x1, y1, z1, x2, y2, z2);
                        claimObj.setName(name);

                        getClaims().add(claimObj);
                    }
                }
            } else if (identifier.equalsIgnoreCase("Allies")) {
                for (String ally : lineParts) {
                    ally = ally.replace("[", "").replace("]", "");

                    if (ally.length() != 0) {
                        allies.add(new ObjectId(ally.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("RequestedAllies")) {
                for (String requestedAlly : lineParts) {
                    requestedAlly = requestedAlly.replace("[", "").replace("]", "");

                    if (requestedAlly.length() != 0) {
                        requestedAllies.add(new ObjectId(requestedAlly.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("Subclaims")) {
                for (String subclaim : lineParts) {
                    subclaim = subclaim.replace("[", "").replace("]", "");

                    if (subclaim.contains(":")) {
                        String[] split = subclaim.split(":");

                        int x1 = Integer.parseInt(split[0].trim());
                        int y1 = Integer.parseInt(split[1].trim());
                        int z1 = Integer.parseInt(split[2].trim());
                        int x2 = Integer.parseInt(split[3].trim());
                        int y2 = Integer.parseInt(split[4].trim());
                        int z2 = Integer.parseInt(split[5].trim());
                        String name = split[6].trim();
                        String membersRaw = "";

                        if (split.length >= 8) {
                            membersRaw = split[7].trim();
                        }

                        Location location1 = new Location(Foxtrot.getInstance().getServer().getWorld("world"), x1, y1, z1);
                        Location location2 = new Location(Foxtrot.getInstance().getServer().getWorld("world"), x2, y2, z2);
                        List<UUID> members = new ArrayList<>();

                        for (String uuidString : membersRaw.split(", ")) {
                            if (uuidString.isEmpty()) {
                                continue;
                            }

                            members.add(UUID.fromString(uuidString.trim()));
                        }

                        Subclaim subclaimObj = new Subclaim(location1, location2, name);
                        subclaimObj.setMembers(members);

                        getSubclaims().add(subclaimObj);
                    }
                }
            } else if (identifier.equalsIgnoreCase("Announcement")) {
                setAnnouncement(lineParts[0]);
            }
        }

        for (UUID member : members) {
            FrozenUUIDCache.ensure(member);
        }

        if (uniqueId == null) {
            uniqueId = new ObjectId();
            Foxtrot.getInstance().getLogger().info("Generating UUID for team " + getName() + "...");
        }

        loading = false;
        needsSave = false;
    }

    public String saveString(boolean toJedis) {
        if (toJedis) {
            needsSave = false;
        }

        if (loading) {
            return (null);
        }

        StringBuilder teamString = new StringBuilder();

        StringBuilder members = new StringBuilder();
        StringBuilder captains = new StringBuilder();
        StringBuilder invites = new StringBuilder();

        for (UUID member : getMembers()) {
            members.append(member.toString()).append(", ");
        }

        for (UUID captain : getCaptains()) {
            captains.append(captain.toString()).append(", ");
        }

        for (UUID invite : getInvitations()) {
            invites.append(invite.toString()).append(", ");
        }

        if (members.length() > 2) {
            members.setLength(members.length() - 2);
        }

        if (captains.length() > 2) {
            captains.setLength(captains.length() - 2);
        }

        if (invites.length() > 2) {
            invites.setLength(invites.length() - 2);
        }

        teamString.append("UUID:").append(getUniqueId().toString()).append("\n");
        teamString.append("Owner:").append(getOwner()).append('\n');
        teamString.append("Captains:").append(captains.toString()).append('\n');
        teamString.append("Members:").append(members.toString()).append('\n');
        teamString.append("Invited:").append(invites.toString().replace("\n", "")).append('\n');
        teamString.append("Subclaims:").append(getSubclaims().toString().replace("\n", "")).append('\n');
        teamString.append("Claims:").append(getClaims().toString().replace("\n", "")).append('\n');
        teamString.append("Allies:").append(getAllies().toString()).append('\n');
        teamString.append("RequestedAllies:").append(getRequestedAllies().toString()).append('\n');
        teamString.append("DTR:").append(getDTR()).append('\n');
        teamString.append("Balance:").append(getBalance()).append('\n');
        teamString.append("DTRCooldown:").append(getDTRCooldown()).append('\n');
        teamString.append("FriendlyName:").append(getName().replace("\n", "")).append('\n');
        teamString.append("Announcement:").append(String.valueOf(getAnnouncement()).replace("\n", "")).append("\n");

        if (getHQ() != null) {
            teamString.append("HQ:").append(getHQ().getWorld().getName()).append(",").append(getHQ().getX()).append(",").append(getHQ().getY()).append(",").append(getHQ().getZ()).append(",").append(getHQ().getYaw()).append(",").append(getHQ().getPitch()).append('\n');
        }

        return (teamString.toString());
    }


    public BasicDBObject toJSON() {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("_id", getUniqueId());
        dbObject.put("Owner", getOwner() == null ? null : getOwner().toString());
        dbObject.put("Captains", UUIDUtils.uuidsToStrings(getCaptains()));
        dbObject.put("Members", UUIDUtils.uuidsToStrings(getMembers()));
        dbObject.put("Invitations", UUIDUtils.uuidsToStrings(getInvitations()));
        dbObject.put("Allies", getAllies());
        dbObject.put("RequestedAllies", getRequestedAllies());
        dbObject.put("DTR", getDTR());
        dbObject.put("DTRCooldown", new Date(getDTRCooldown()));
        dbObject.put("Balance", getBalance());
        dbObject.put("Name", getName());
        dbObject.put("HQ", LocationSerializer.serialize(getHQ()));
        dbObject.put("Announcement", getAnnouncement());

        BasicDBList claims = new BasicDBList();
        BasicDBList subclaims = new BasicDBList();

        for (Claim claim : getClaims()) {
            claims.add(claim.json());
        }

        for (Subclaim subclaim : getSubclaims()) {
            subclaims.add(subclaim.json());
        }

        dbObject.put("Claims", claims);
        dbObject.put("Subclaims", subclaims);

        return (dbObject);
    }

    public BasicDBObject getJSONIdentifier() {
        return (new BasicDBObject("_id", getUniqueId()));
    }

    private Location parseLocation(String[] args) {
        if (args.length != 6) {
            return (null);
        }

        World world = Foxtrot.getInstance().getServer().getWorld(args[0]);
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);
        double z = Double.parseDouble(args[3]);
        float yaw = Float.parseFloat(args[4]);
        float pitch = Float.parseFloat(args[5]);

        return (new Location(world, x, y, z, yaw, pitch));
    }

    public void pushToMongoLog(BasicDBObject toLog) {
        DBCollection teamLogCollection = Foxtrot.getInstance().getMongoPool().getDB("HCTeams").getCollection("TeamLog");

        toLog.put("Team", getUniqueId().toString());
        toLog.put("TeamName", getName());
        toLog.put("Date", new Date());

        teamLogCollection.insert(toLog);
    }

    public void sendTeamInfo(Player player) {
        // Don't make our null teams have DTR....
        // @HCFactions
        if (getOwner() == null) {
            player.sendMessage(GRAY_LINE);

            if (hasDTRBitmask(DTRBitmask.KOTH)) {
                player.sendMessage(ChatColor.AQUA + getName() + ChatColor.GOLD + " KOTH");
            } else if (hasDTRBitmask(DTRBitmask.CITADEL)) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Citadel");
            } else {
                player.sendMessage(ChatColor.BLUE + getName());
            }

            player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));
            player.sendMessage(GRAY_LINE);
            return;
        }

        KillsMap killsMap = Foxtrot.getInstance().getKillsMap();
        Player owner = Foxtrot.getInstance().getServer().getPlayer(getOwner());
        StringBuilder allies = new StringBuilder();
        StringBuilder members = new StringBuilder();
        StringBuilder captains = new StringBuilder();
        int onlineMembers = 0;

        for (ObjectId allyId : getAllies()) {
            Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                allies.append(ally.getName(player)).append(ChatColor.YELLOW).append("[").append(ChatColor.GREEN).append(ally.getOnlineMemberAmount()).append("/").append(ally.getSize()).append(ChatColor.YELLOW).append("]").append(ChatColor.GRAY).append(", ");
            }
        }

        for (Player onlineMember : getOnlineMembers()) {
            onlineMembers++;

            // There can only be one owner, so we special case it.
            if (isOwner(onlineMember.getUniqueId())) {
                continue;
            }

            String memberString = ChatColor.GREEN + onlineMember.getName() + ChatColor.YELLOW + "[" + ChatColor.GREEN + killsMap.getKills(onlineMember.getUniqueId()) + ChatColor.YELLOW + "]";

            if (isCaptain(onlineMember.getUniqueId())) {
                captains.append(memberString).append(ChatColor.GRAY).append(", ");
            } else {
                members.append(memberString).append(ChatColor.GRAY).append(", ");
            }
        }

        for (UUID offlineMember : getOfflineMembers()) {
            if (isOwner(offlineMember)) {
                continue;
            }

            String memberString = ChatColor.GRAY + UUIDUtils.name(offlineMember) + ChatColor.YELLOW + "[" + ChatColor.GREEN + killsMap.getKills(offlineMember) + ChatColor.YELLOW + "]";

            if (isCaptain(offlineMember)) {
                captains.append(memberString).append(ChatColor.GRAY).append(", ");
            } else {
                members.append(memberString).append(ChatColor.GRAY).append(", ");
            }
        }

        // Now we can actually send all that info we just processed.
        player.sendMessage(GRAY_LINE);
        player.sendMessage(ChatColor.BLUE + getName() + ChatColor.GRAY + " [" + onlineMembers + "/" + getSize() + "]" + ChatColor.DARK_AQUA + " - " + ChatColor.YELLOW + "HQ: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));

        if (allies.length() > 2) {
            allies.setLength(allies.length() - 2);
            player.sendMessage(ChatColor.YELLOW + "Allies: " + allies.toString());
        }

        player.sendMessage(ChatColor.YELLOW + "Leader: " + (owner == null || owner.hasMetadata("invisible") ? ChatColor.GRAY : ChatColor.GREEN) + UUIDUtils.name(getOwner()) + ChatColor.YELLOW + "[" + ChatColor.GREEN + killsMap.getKills(getOwner()) + ChatColor.YELLOW + "]");

        if (captains.length() > 2) {
            captains.setLength(captains.length() - 2);
            player.sendMessage(ChatColor.YELLOW + "Captains: " + captains.toString());
        }

        if (members.length() > 2) {
            members.setLength(members.length() - 2);
            player.sendMessage(ChatColor.YELLOW + "Members: " + members.toString());
        }

        player.sendMessage(ChatColor.YELLOW + "Balance: " + ChatColor.BLUE + "$" + Math.round(getBalance()));
        player.sendMessage(ChatColor.YELLOW + "Deaths until Raidable: " + getDTRColor() + DTR_FORMAT.format(getDTR()) + getDTRSuffix());

        if (DTRHandler.isOnCooldown(this)) {
            player.sendMessage(ChatColor.YELLOW + "Time Until Regen: " + ChatColor.BLUE + TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000).trim());
        }

        // Only show this if they're a member.
        if (isMember(player.getUniqueId()) && announcement != null) {
            player.sendMessage(ChatColor.YELLOW + "Announcement: " + ChatColor.LIGHT_PURPLE + announcement);
        }

        player.sendMessage(GRAY_LINE);
        // .... and that is how we do a /f who.
    }

    @Override
    public int hashCode() {
        return (getUniqueId().hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Team) {
            return ((Team) obj).getUniqueId().equals(getUniqueId());
        }

        return (super.equals(obj));
    }

    public ChatColor getDTRColor() {
        ChatColor dtrColor = ChatColor.GREEN;

        if (DTR / getMaxDTR() <= 0.25) {
            if (isRaidable()) {
                dtrColor = ChatColor.DARK_RED;
            } else {
                dtrColor = ChatColor.YELLOW;
            }
        }

        return (dtrColor);
    }

    public String getDTRSuffix() {
        if (DTRHandler.isRegenerating(this)) {
            if (getOnlineMemberAmount() == 0) {
                return (ChatColor.GRAY + "◀");
            } else {
                return (ChatColor.GREEN + "▲");
            }
        } else if (DTRHandler.isOnCooldown(this)) {
            return (ChatColor.RED + "■");
        } else {
            return (ChatColor.GREEN + "◀");
        }
    }

}