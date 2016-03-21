package net.frozenorb.foxtrot.team;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.maps.DeathbanMap;
import net.frozenorb.foxtrot.persist.maps.KillsMap;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.redis.RedisCommand;
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
import redis.clients.jedis.Jedis;

import java.text.DecimalFormat;
import java.util.*;

public class Team {

    // Constants //
    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
    public static final String GRAY_LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);
    public static final ChatColor ALLY_COLOR = ChatColor.BLUE;
    public static final int MAX_CLAIMS = 2;
    public static final int MAX_FORCE_INVITES = 5;

    // Internal //
    @Getter private boolean needsSave = false;
    @Getter private boolean loading = false;

    // Persisted //
    @Getter @Setter private ObjectId uniqueId;
    @Getter private String name;
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
    @Getter private int maxOnline = -1;

    @Getter @Setter private int forceInvites = MAX_FORCE_INVITES;
    @Getter private Set<UUID> historicalMembers = new HashSet<>(); // this will store all players that were once members

    // Not persisted //
    @Getter @Setter private UUID focused;

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
        if (name.equals("glowstone")) {
            return ChatColor.YELLOW + "Glowstone Mountain";
        } else if (owner == null) {
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
            } else if (hasDTRBitmask(DTRBitmask.ROAD)) {
                return (ChatColor.RED + getName().replace("Road", " Road"));
            } else if (hasDTRBitmask(DTRBitmask.CONQUEST)) {
                return (ChatColor.YELLOW + "Conquest");
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
        historicalMembers.add(member);
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

    public void setMaxOnline(int maxOnline) {
        this.maxOnline = maxOnline;
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
                Foxtrot.getInstance().getLogger().info("Economy Logger: Depositing " + balance + " into " + UUIDUtils.name(owner) + "'s account: Disbanded team");
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
                qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

                    @Override
                    public Object execute(Jedis redis) {
                        redis.del("fox_teams." + name.toLowerCase());
                        return (null);
                    }

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

        qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                redis.del("fox_teams." + oldName.toLowerCase());
                return (null);
            }

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

    public void validateAllies() {
        Iterator<ObjectId> allyIterator = getAllies().iterator();

        while (allyIterator.hasNext()) {
            ObjectId ally = allyIterator.next();
            Team checkTeam = Foxtrot.getInstance().getTeamHandler().getTeam(ally);

            if (checkTeam == null) {
                allyIterator.remove();
            }
        }
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
            DTRCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeRaidable();
        } else {
            DTRCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeDeath();
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
            if (line.indexOf(':') == -1) {
                System.out.println("Found an invalid line... `" + line + "`");
                continue;
            }

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
            } else if (identifier.equalsIgnoreCase("HistoricalMembers")) {
                for (String name : lineParts) {
                    if (name.length() >= 2) {
                        getHistoricalMembers().add(UUID.fromString(name.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("HQ")) {
                setHQ(parseLocation(lineParts));
            } else if (identifier.equalsIgnoreCase("DTR")) {
                setDTR(Double.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Balance")) {
                setBalance(Double.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("MaxOnline")) {
                setMaxOnline(Integer.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ForceInvites")) {
                setForceInvites(Integer.valueOf(lineParts[0]));
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
                // Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
                if (Foxtrot.getInstance().getMapHandler().getAllyLimit() == 0) {
                    continue;
                }

                for (String ally : lineParts) {
                    ally = ally.replace("[", "").replace("]", "");

                    if (ally.length() != 0) {
                        allies.add(new ObjectId(ally.trim()));
                    }
                }
            } else if (identifier.equalsIgnoreCase("RequestedAllies")) {
                // Just cancel loading of allies if they're disabled (for switching # of allowed allies mid-map)
                if (Foxtrot.getInstance().getMapHandler().getAllyLimit() == 0) {
                    continue;
                }

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
        StringBuilder historicalMembers = new StringBuilder();

        for (UUID member : getMembers()) {
            members.append(member.toString()).append(", ");
        }

        for (UUID captain : getCaptains()) {
            captains.append(captain.toString()).append(", ");
        }

        for (UUID invite : getInvitations()) {
            invites.append(invite.toString()).append(", ");
        }

        for (UUID member : getHistoricalMembers()) {
            historicalMembers.append(member.toString()).append(", ");
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

        if (historicalMembers.length() > 2) {
            historicalMembers.setLength(historicalMembers.length() - 2);
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
        teamString.append("HistoricalMembers:").append(historicalMembers.toString()).append('\n');
        teamString.append("DTR:").append(getDTR()).append('\n');
        teamString.append("Balance:").append(getBalance()).append('\n');
        teamString.append("MaxOnline:").append(getMaxOnline()).append('\n');
        teamString.append("ForceInvites:").append(getForceInvites()).append('\n');
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
        dbObject.put("MaxOnline", getMaxOnline());
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

    public void sendMessage(String message) {
        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (isMember(player.getUniqueId())) {
                player.sendMessage(message);
            }
        }
    }

    public void pushToMongoLog(BasicDBObject toLog) {
//        new BukkitRunnable() {
//
//            public void run() {
//                System.out.print("updating for " + name + ": " + isLoading());
//                if (isLoading() || getName() == null || getUniqueId() == null) {
//                    return;
//                }
//
//                DBCollection teamLogCollection = Foxtrot.getInstance().getMongoPool().getDB("HCTeams").getCollection("TeamLog");
//
//                toLog.put("Team", getUniqueId().toString());
//                toLog.put("TeamName", getName());
//                toLog.put("Date", new Date());
//
//                teamLogCollection.insert(toLog);
//            }
//
//        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public void sendTeamInfo(Player player) {
        // Don't make our null teams have DTR....
        // @HCFactions
        if (getOwner() == null) {
            player.sendMessage(GRAY_LINE);
            player.sendMessage(getName(player));
            player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));
            player.sendMessage(GRAY_LINE);
            return;
        }

        KillsMap killsMap = Foxtrot.getInstance().getKillsMap();
        DeathbanMap deathbanMap = Foxtrot.getInstance().getDeathbanMap();
        Player owner = Foxtrot.getInstance().getServer().getPlayer(getOwner());
        StringBuilder allies = new StringBuilder();

        FancyMessage captainsJson = new FancyMessage("Captains: ").color(ChatColor.YELLOW);

        if (player.hasPermission("basic.staff")) {
            captainsJson.command("/manageteam demote " + getName()).tooltip("§bClick to demote captains");
        }

        FancyMessage membersJson = new FancyMessage("Members: ").color(ChatColor.YELLOW);

        if (player.hasPermission("basic.staff")) {
            membersJson.command("/manageteam promote " + getName()).tooltip("§bClick to promote members");
        }


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

            boolean captain = isCaptain(onlineMember.getUniqueId());

            FancyMessage appendTo = captain ? captainsJson : membersJson;

            if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                appendTo.then(", ").color(ChatColor.GRAY);
            }

            appendTo.then(onlineMember.getName()).color(ChatColor.GREEN).then("[").color(ChatColor.YELLOW);
            appendTo.then(killsMap.getKills(onlineMember.getUniqueId()) + "").color(ChatColor.GREEN);
            appendTo.then("]").color(ChatColor.YELLOW);

        }

        for (UUID offlineMember : getOfflineMembers()) {
            if (isOwner(offlineMember)) {
                continue;
            }

            boolean captain = isCaptain(offlineMember);

            FancyMessage appendTo = captain ? captainsJson : membersJson;

            if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                appendTo.then(", ").color(ChatColor.GRAY);
            }

            appendTo.then(UUIDUtils.name(offlineMember)).color(deathbanMap.isDeathbanned(offlineMember) ? ChatColor.RED : ChatColor.GRAY);
            appendTo.then("[").color(ChatColor.YELLOW).then("" + killsMap.getKills(offlineMember)).color(ChatColor.GREEN);
            appendTo.then("]").color(ChatColor.YELLOW);

        }

        // Now we can actually send all that info we just processed.
        player.sendMessage(GRAY_LINE);

        FancyMessage teamLine = new FancyMessage();

        teamLine.text(ChatColor.BLUE + getName()).link("http://www.HCTeams.com/teams/" + getName()).tooltip(ChatColor.GREEN + "Click to view team on the HCTeams website!");
        teamLine.then().text(ChatColor.GRAY + " [" + onlineMembers + "/" + getSize() + "]" + ChatColor.DARK_AQUA + " - ");
        teamLine.then().text(ChatColor.YELLOW + "HQ: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));

        if (HQ != null && player.hasPermission("basic.staff")) {
            teamLine.command("/tppos " + HQ.getBlockX() + " " + HQ.getBlockY() + " " + HQ.getBlockZ());
            teamLine.tooltip("§aClick to warp to HQ");

        }

        if (player.hasPermission("basic.staff")) {
            teamLine.then().text("§3 - §e[Manage]").color(ChatColor.YELLOW).command("/manageteam manage " + getName()).tooltip("§bClick to manage team");
        }

        teamLine.send(player);

        if (allies.length() > 2) {
            allies.setLength(allies.length() - 2);
            player.sendMessage(ChatColor.YELLOW + "Allies: " + allies.toString());
        }

        FancyMessage leader = new FancyMessage(ChatColor.YELLOW + "Leader: " + (owner == null || owner.hasMetadata("invisible") ? (deathbanMap.isDeathbanned(getOwner()) ? ChatColor.RED : ChatColor.GRAY) : ChatColor.GREEN) + UUIDUtils.name(getOwner()) + ChatColor.YELLOW + "[" + ChatColor.GREEN + killsMap.getKills(getOwner()) + ChatColor.YELLOW + "]");


        if (player.hasPermission("basic.staff")) {
            leader.command("/manageteam leader " + getName()).tooltip("§bClick to change leader");
        }

        leader.send(player);

        if (!ChatColor.stripColor(captainsJson.toOldMessageFormat()).endsWith("s: ")) {
            captainsJson.send(player);
        }


        if (!ChatColor.stripColor(membersJson.toOldMessageFormat()).endsWith("s: ")) {
            membersJson.send(player);
        }


        FancyMessage balance = new FancyMessage(ChatColor.YELLOW + "Balance: " + ChatColor.BLUE + "$" + Math.round(getBalance()));

        if (player.hasPermission("basic.staff")) {
            balance.command("/manageteam balance " + getName()).tooltip("§bClick to modify team balance");
        }

        balance.send(player);


        FancyMessage dtrMessage = new FancyMessage(ChatColor.YELLOW + "Deaths until Raidable: " + getDTRColor() + DTR_FORMAT.format(getDTR()) + getDTRSuffix());


        if (player.hasPermission("basic.staff")) {
            dtrMessage.command("/manageteam dtr " + getName()).tooltip("§bClick to modify team DTR");
        }

        dtrMessage.send(player);

        if (isMember(player.getUniqueId()) || player.hasPermission("basic.staff")) {
            player.sendMessage(ChatColor.YELLOW + "Force Invites: " + ChatColor.RED + getForceInvites());
        }

        if (DTRHandler.isOnCooldown(this)) {
            if (!player.isOp()) {
                player.sendMessage(ChatColor.YELLOW + "Time Until Regen: " + ChatColor.BLUE + TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000).trim());
            } else {
                FancyMessage message = new FancyMessage(ChatColor.YELLOW + "Time Until Regen: ")
                        .tooltip(ChatColor.GREEN + "Click to remove regeneration timer").command("/startdtrregen " + getName());

                message.then(TimeUtils.formatIntoDetailedString(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000)).color(ChatColor.BLUE)
                        .tooltip(ChatColor.GREEN + "Click to remove regeneration timer").command("/startdtrregen " + getName());

                message.send(player);
            }
        }

        // Only show this if they're a member.
        if (isMember(player.getUniqueId()) && announcement != null && !announcement.equals("null")) {
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