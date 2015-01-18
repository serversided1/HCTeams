package net.frozenorb.foxtrot.team;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.game.CTFFlag;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import net.frozenorb.foxtrot.serialization.serializers.LocationSerializer;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.persist.KillsMap;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.mBasic.Basic;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Team {

    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
    public static final String GRAY_LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);

    public static final ChatColor ALLY_COLOR = ChatColor.BLUE;

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
    @Getter private String owner = null;
    @Getter private double balance;
    @Getter private double DTR;
    @Getter private long DTRCooldown;
    @Getter private List<Claim> claims = new ArrayList<Claim>();
    @Getter private List<Subclaim> subclaims = new ArrayList<Subclaim>();
    @Getter private Set<String> members = new HashSet<String>();
    @Getter private Set<String> captains = new HashSet<String>();
    @Getter private Set<String> invitations = new HashSet<String>();
    @Getter private Set<ObjectId> allies = new HashSet<ObjectId>();
    @Getter private Set<ObjectId> requestedAllies = new HashSet<ObjectId>();
    @Getter @Setter private float DTRRegenMultiplier = 1F; // We're safe to use a @Setter here as this value isn't persisted.

    public Team(String name) {
        this.name = name;
    }

    public void setDTR(double newDTR) {
        if (DTR == newDTR) {
            return;
        }

        if (DTR <= 0 && newDTR > 0) {
            TeamActionTracker.logAction(this, TeamActionType.GENERAL, "Team no longer raidable.");
        }

        if (DTRRegenMultiplier != 1F && newDTR == getMaxDTR()) {
            TeamActionTracker.logAction(this, TeamActionType.GENERAL, "DTR Regen Multiplier: Deactivated as team is max DTR. [DTR Regen Multiplier: " + DTRRegenMultiplier + ", DTR: " + newDTR + "]");
            DTRRegenMultiplier = 1F;

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + "Your team's DTR regen multiplier has been deactivated as your team has reached max DTR.");
                }
            }
        }

        if (!isLoading()) {
            FoxtrotPlugin.getInstance().getLogger().info("[DTR Change] " + getName() + ": " + DTR + " --> " + newDTR);
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
            if (hasDTRBitmask(DTRBitmaskType.SAFE_ZONE)) {
                switch (player.getWorld().getEnvironment()) {
                    case NETHER:
                        return (ChatColor.GREEN + "Nether Spawn");
                    case THE_END:
                        return (ChatColor.GREEN + "The End Spawn");

                        /*if (hasDTRBitmask(DTRBitmaskType.DENY_REENTRY)) {
                            return (ChatColor.GREEN + "The End Spawn");
                        } else {
                            return (ChatColor.GREEN + "The End Exit");
                        }*/
                }

                return (ChatColor.GREEN + "Spawn");
            } else if (hasDTRBitmask(DTRBitmaskType.KOTH)) {
                return (ChatColor.AQUA + getName() + ChatColor.GOLD + " KOTH");
            } else if (hasDTRBitmask(DTRBitmaskType.ROAD)) {
                return (ChatColor.RED + getName().replaceAll("Road", " Road"));
            } else if (hasDTRBitmask(DTRBitmaskType.CITADEL_COURTYARD)) {
                return (ChatColor.DARK_PURPLE + "Citadel Courtyard");
            } else if (hasDTRBitmask(DTRBitmaskType.CITADEL_KEEP)) {
                return (ChatColor.DARK_PURPLE + "Citadel Keep");
            } else if (hasDTRBitmask(DTRBitmaskType.CITADEL_TOWN)) {
                return (ChatColor.DARK_PURPLE + "Citadel Town");
            }
        }

        if (isMember(player)) {
            return (ChatColor.GREEN + getName());
        } else if (isAlly(player)) {
            return (Team.ALLY_COLOR + getName());
        } else {
            return (ChatColor.RED + getName());
        }
    }

    public void addMember(String member) {
        if (member.equalsIgnoreCase("null")) {
            return;
        }

        members.add(member);
        TeamActionTracker.logAction(this, TeamActionType.GENERAL, "Member Added: " + member);
        flagForSave();
    }

    public void addCaptain(String captain) {
        captains.add(captain);
        TeamActionTracker.logAction(this, TeamActionType.GENERAL, "Captain Added: " + captain);
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

    public void removeCaptain(String name) {
        Iterator<String> iterator = captains.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().equalsIgnoreCase(name)) {
                iterator.remove();
            }
        }

        TeamActionTracker.logAction(this, TeamActionType.GENERAL, "Captain Removed: " + name);
        flagForSave();
    }

    public void setOwner(String owner) {
        String oldOwner = this.owner;
        this.owner = owner;

        if (owner != null && !owner.equals("null")) {
            members.add(owner);
        }

        TeamActionTracker.logAction(this, TeamActionType.GENERAL, "Owner Changed: " + oldOwner + " -> " + owner);
        flagForSave();
    }

    public void setHQ(Location hq) {
        String oldHQ = this.HQ == null ? "None" : (getHQ().getBlockX() + ", " + getHQ().getBlockY() + ", " + getHQ().getBlockZ());
        String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
        this.HQ = hq;
        TeamActionTracker.logAction(this, TeamActionType.GENERAL, "HQ Changed: [" + oldHQ + "] -> [" + newHQ + "]");
        flagForSave();
    }

    public void disband() {
        try {
            if (owner != null) {
                Basic.get().getEconomyManager().depositPlayer(owner, balance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ObjectId allyId : getAllies()) {
            Team ally = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                ally.getAllies().remove(getUniqueId());
            }
        }

        FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(this);
        LandBoard.getInstance().clear(this);

        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                jedis.del("fox_teams." + name.toLowerCase());
                return (null);
            }

        });

        needsSave = false;
    }

    public void rename(String newName) {
        final String oldName = name;

        FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(this);

        this.name = newName;

        FoxtrotPlugin.getInstance().getTeamHandler().setupTeam(this);

        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

            public Object execute(Jedis jedis) {
                jedis.del("fox_teams." + oldName.toLowerCase());
                return (null);
            }

        });

        for (Claim claim : getClaims()) {
            claim.setName(claim.getName().replaceAll(oldName, newName));
        }

        flagForSave();
    }

    public void flagForSave() {
        needsSave = true;
    }

    public boolean isOwner(String name) {
        return (owner != null && owner.equalsIgnoreCase(name));
    }

    public String getActualPlayerName(String pName) {
        for (String str : members) {
            if (pName.equalsIgnoreCase(str)) {
                return (str);
            }
        }

        return (null);
    }

    public boolean isMember(Player player) {
        return (isMember(player.getName()));
    }

    public boolean isMember(String name) {
        for (String member : members) {
            if (name.equalsIgnoreCase(member)) {
                return (true);
            }
        }

        return (false);
    }

    public boolean isCaptain(String name) {
        for (String member : captains) {
            if (name.equalsIgnoreCase(member)) {
                return (true);
            }
        }

        return (false);
    }

    public boolean isAlly(Player player) {
        return (isAlly(player.getName()));
    }

    public boolean isAlly(String name) {
        for (ObjectId ally : getAllies()) {
            Team allyTeam = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(ally);

            if (allyTeam != null && allyTeam.isMember(name)) {
                return (true);
            }
        }

        return (false);
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

    public boolean removeMember(String name) {
        Iterator<String> membersIterator = members.iterator();

        while (membersIterator.hasNext()) {
            String member = membersIterator.next();

            if (member.equalsIgnoreCase(name)) {
                membersIterator.remove();
                break;
            }
        }

        removeCaptain(name);

        if (isOwner(name)) {
            membersIterator = members.iterator();

            if (membersIterator.hasNext()) {
                this.owner = membersIterator.next();
            } else {
                this.owner = null;
            }
        }

        Iterator<Subclaim> subclaimIterator = subclaims.iterator();

        while (subclaimIterator.hasNext()) {
            Subclaim subclaim = subclaimIterator.next();

            if (subclaim.isMember(name)) {
                subclaim.removeMember(name);
            }
        }

        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game != null) {
            for (CTFFlag flag : game.getFlags().values()) {
                if (flag.getFlagHolder() != null && flag.getFlagHolder().getName().equalsIgnoreCase(name)) {
                    flag.dropFlag(false);
                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(CTFHandler.PREFIX + " " + ChatColor.WHITE + name + ChatColor.YELLOW + " has dropped the " + flag.getColor().getChatColor() + flag.getColor().getName() + " Flag" + ChatColor.YELLOW + "!");
                }
            }
        }

        if (DTR > getMaxDTR()) {
            DTR = getMaxDTR();
        }

        TeamActionTracker.logAction(this, TeamActionType.GENERAL, "Member Removed: " + name);
        flagForSave();
        return (owner == null || members.size() == 0);
    }

    public boolean hasDTRBitmask(DTRBitmaskType bitmaskType) {
        if (getOwner() != null) {
            return (false);
        }

        int dtrInt = (int) DTR;
        return (((dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask()));
    }

    public int getOnlineMemberAmount() {
        int amt = 0;

        for (String members : getMembers()) {
            Player exactPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(members);

            if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
                amt++;
            }
        }

        return (amt);
    }

    public List<Player> getOnlineMembers() {
        List<Player> players = new ArrayList<Player>();

        for (String member : getMembers()) {
            if (member == null) {
                continue;
            }

            Player exactPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(member);

            if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
                players.add(exactPlayer);
            }
        }

        return (players);
    }

    public List<String> getOfflineMembers() {
        List<String> players = new ArrayList<String>();

        for (String member : getMembers()) {
            if (member == null) {
                continue;
            }

            Player exactPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(member);

            if (exactPlayer == null || exactPlayer.hasMetadata("invisible")) {
                players.add(member);
            }
        }

        return (players);
    }

    public Subclaim getSubclaim(String name) {
        for (Subclaim sc : subclaims) {
            if (sc.getName().equalsIgnoreCase(name)) {
                return (sc);
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

    public void playerDeath(String p, double dtrLoss) {
        double newDTR = Math.max(DTR - dtrLoss, -.99);
        TeamActionTracker.logAction(this, TeamActionType.GENERAL, "Member Death: " + p + " [DTR Loss: " + dtrLoss + ", Old DTR: " + DTR + ", New DTR: " + newDTR + "]");

        for (Player player : getOnlineMembers()) {
            player.sendMessage(ChatColor.RED + "Member Death: " + ChatColor.WHITE + p);
            player.sendMessage(ChatColor.RED + "DTR: " + ChatColor.WHITE + DTR_FORMAT.format(newDTR));
        }

        if (DTRRegenMultiplier != 1F) {
            TeamActionTracker.logAction(this, TeamActionType.GENERAL, "DTR Regen Multiplier: Deactivated as " + p + " died. [DTR Regen Multiplier: " + DTRRegenMultiplier + "]");
            DTRRegenMultiplier = 1F;

            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + "Your team's DTR regen multiplier has been deactivated as a member has died.");
                }
            }
        }

        FoxtrotPlugin.getInstance().getLogger().info("[TeamDeath] " + name + " > " + "Player death: [" + p + "]");
        setDTR(newDTR);

        if (isRaidable()) {
            TeamActionTracker.logAction(this, TeamActionType.GENERAL, "Team now raidable.");
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
        dtrPerHour *= DTRRegenMultiplier;
        return (dtrPerHour / 60);
    }

    public double getMaxDTR() {
        return (DTRHandler.getMaxDTR(getSize()));
    }

    public void load(String str) {
        loading = true;
        String[] lines = str.split("\n");

        for (String line : lines) {
            String identifier = line.substring(0, line.indexOf(':'));
            String[] lineParts = line.substring(line.indexOf(':') + 1).split(",");

            if (identifier.equalsIgnoreCase("Owner")) {
                if (!lineParts[0].equals("null")) {
                    setOwner(lineParts[0]);
                }
            } else if (identifier.equalsIgnoreCase("UUID")) {
                uniqueId = new ObjectId(lineParts[0].trim());
            } else if (identifier.equalsIgnoreCase("Members")) {
                for (String name : lineParts) {
                    if (name.length() >= 2 && !name.equalsIgnoreCase("null")) {
                        addMember(name.trim());
                    }
                }
            } else if (identifier.equalsIgnoreCase("Captains")) {
                for (String name : lineParts) {
                    if (name.length() >= 2 && !name.equalsIgnoreCase("null")) {
                        addCaptain(name.trim());
                    }
                }
            } else if (identifier.equalsIgnoreCase("Invited")) {
                for (String name : lineParts) {
                    if (name.length() >= 2 && !name.equalsIgnoreCase("null")) {
                        getInvitations().add(name);
                    }
                }
            } else if (identifier.equalsIgnoreCase("HQ")) {
                setHQ(parseLocation(lineParts));
            } else if (identifier.equalsIgnoreCase("DTR")) {
                setDTR(Double.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Balance")) {
                setBalance(Double.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DTRCooldown")) {
                setDTRCooldown(Long.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FriendlyName")) {
                setName(lineParts[0]);
            } else if (identifier.equalsIgnoreCase("Claims")) {
                for (String claim : lineParts) {
                    claim = claim.replace("[", "").replace("]", "");

                    if (claim.contains(":")) {
                        String[] split = claim.split(":");

                        int x1 = Integer.valueOf(split[0].trim());
                        int y1 = Integer.valueOf(split[1].trim());
                        int z1 = Integer.valueOf(split[2].trim());
                        int x2 = Integer.valueOf(split[3].trim());
                        int y2 = Integer.valueOf(split[4].trim());
                        int z2 = Integer.valueOf(split[5].trim());
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

                        int x1 = Integer.valueOf(split[0].trim());
                        int y1 = Integer.valueOf(split[1].trim());
                        int z1 = Integer.valueOf(split[2].trim());
                        int x2 = Integer.valueOf(split[3].trim());
                        int y2 = Integer.valueOf(split[4].trim());
                        int z2 = Integer.valueOf(split[5].trim());
                        String name = split[6].trim();
                        String members = "";

                        if (split.length >= 8) {
                            members = split[7].trim();
                        }

                        Location loc1 = new Location(FoxtrotPlugin.getInstance().getServer().getWorld("world"), x1, y1, z1);
                        Location loc2 = new Location(FoxtrotPlugin.getInstance().getServer().getWorld("world"), x2, y2, z2);

                        Subclaim subclaimObj = new Subclaim(loc1, loc2, name);
                        subclaimObj.setMembers(new ArrayList<String>(Arrays.asList(members.split(","))));

                        getSubclaims().add(subclaimObj);
                    }
                }
            }
        }

        if (uniqueId == null) {
            uniqueId = new ObjectId();
            FoxtrotPlugin.getInstance().getLogger().info("Generating UUID for team " + getName() + "...");
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

        for (String member : getMembers()) {
            members.append(member.trim()).append(", ");
        }

        for (String captain : getCaptains()) {
            captains.append(captain.trim()).append(", ");
        }

        for (String invite : getInvitations()) {
            invites.append(invite.trim()).append(", ");
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
        teamString.append("Invited:").append(invites.toString()).append('\n');
        teamString.append("Subclaims:").append(getSubclaims().toString()).append('\n');
        teamString.append("Claims:").append(getClaims().toString()).append('\n');
        teamString.append("Allies:").append(getAllies().toString()).append('\n');
        teamString.append("RequestedAllies:").append(getRequestedAllies().toString()).append('\n');
        teamString.append("DTR:").append(getDTR()).append('\n');
        teamString.append("Balance:").append(getBalance()).append('\n');
        teamString.append("DTRCooldown:").append(getDTRCooldown()).append('\n');
        teamString.append("FriendlyName:").append(getName()).append('\n');

        if (getHQ() != null) {
            teamString.append("HQ:").append(getHQ().getWorld().getName()).append(",").append(getHQ().getX()).append(",").append(getHQ().getY()).append(",").append(getHQ().getZ()).append(",").append(getHQ().getYaw()).append(",").append(getHQ().getPitch()).append('\n');
        }

        return (teamString.toString());
    }

    public BasicDBObject json() {
        BasicDBObject dbObject = new BasicDBObject();
        LocationSerializer locationSerializer = new LocationSerializer();

        dbObject.put("_id", getUniqueId());
        dbObject.put("Owner", getOwner());
        dbObject.put("Captains", getCaptains());
        dbObject.put("Members", getMembers());
        dbObject.put("Invitations", getInvitations());
        dbObject.put("Allies", getAllies());
        dbObject.put("RequestedAllies", getRequestedAllies());
        dbObject.put("DTR", getDTR());
        dbObject.put("DTRCooldown", getDTRCooldown());
        dbObject.put("Balance", getBalance());
        dbObject.put("Name", getName());
        dbObject.put("HQ", locationSerializer.serialize(getHQ()));

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

    private Location parseLocation(String[] args) {
        if (args.length != 6) {
            return (null);
        }

        World world = FoxtrotPlugin.getInstance().getServer().getWorld(args[0]);
        double x = Double.parseDouble(args[1]);
        double y = Double.parseDouble(args[2]);
        double z = Double.parseDouble(args[3]);
        float yaw = Float.parseFloat(args[4]);
        float pitch = Float.parseFloat(args[5]);

        return (new Location(world, x, y, z, yaw, pitch));
    }

    public void sendTeamInfo(Player player) {
        // Don't make our null teams have DTR....
        // @HCFactions
        if (getOwner() == null || getOwner().equals("null")) {
            player.sendMessage(GRAY_LINE);

            if (hasDTRBitmask(DTRBitmaskType.KOTH)) {
                player.sendMessage(ChatColor.AQUA + getName() + ChatColor.GOLD + " KOTH");
            } else if (hasDTRBitmask(DTRBitmaskType.CITADEL_TOWN)) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Citadel Town");
            }  else if (hasDTRBitmask(DTRBitmaskType.CITADEL_COURTYARD)) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Citadel Courtyard");
            }  else if (hasDTRBitmask(DTRBitmaskType.CITADEL_KEEP)) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Citadel Keep");
            } else {
                player.sendMessage(ChatColor.BLUE + getName());
            }

            player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (HQ == null ? "None" : HQ.getBlockX() + ", " + HQ.getBlockZ()));
            player.sendMessage(GRAY_LINE);
            return;
        }

        KillsMap killsMap = FoxtrotPlugin.getInstance().getKillsMap();
        Player owner = FoxtrotPlugin.getInstance().getServer().getPlayerExact(getOwner());
        StringBuilder allies = new StringBuilder();
        StringBuilder members = new StringBuilder();
        StringBuilder captains = new StringBuilder();
        int onlineMembers = 0;

        for (ObjectId allyId : getAllies()) {
            Team ally = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(allyId);

            if (ally != null) {
                allies.append(ally.getName(player)).append(ChatColor.YELLOW).append("[").append(ChatColor.GREEN).append(ally.getOnlineMemberAmount()).append("/").append(ally.getSize()).append(ChatColor.YELLOW).append("]").append(ChatColor.GRAY).append(", ");
            }
        }

        for (Player onlineMember : getOnlineMembers()) {
            onlineMembers++;

            if (isOwner(onlineMember.getName())) {
                continue;
            }

            String memberString = ChatColor.GREEN + onlineMember.getName() + ChatColor.YELLOW + "[" + ChatColor.GREEN + killsMap.getKills(onlineMember.getName()) + ChatColor.YELLOW + "]";

            if (isCaptain(onlineMember.getName())) {
                captains.append(memberString).append(ChatColor.GRAY).append(", ");
            } else {
                members.append(memberString).append(ChatColor.GRAY).append(", ");
            }
        }

        for (String offlineMember : getOfflineMembers()) {
            if (isOwner(offlineMember)) {
                continue;
            }

            String memberString = ChatColor.GRAY + offlineMember + ChatColor.YELLOW + "[" + ChatColor.GREEN + killsMap.getKills(offlineMember) + ChatColor.YELLOW + "]";

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

        if (DTRRegenMultiplier != 1F) {
            player.sendMessage(ChatColor.YELLOW + "DTR Regen Multiplier: " + ChatColor.GRAY + DTRRegenMultiplier + "x");
        }

        player.sendMessage(ChatColor.YELLOW + "Leader: " + (owner == null || owner.hasMetadata("invisible") ? ChatColor.GRAY : ChatColor.GREEN) + getOwner() + ChatColor.YELLOW + "[" + ChatColor.GREEN + killsMap.getKills(getOwner()) + ChatColor.YELLOW + "]");

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
            player.sendMessage(ChatColor.YELLOW + "Time Until Regen: " + ChatColor.BLUE + TimeUtils.getConvertedTime(((int) (getDTRCooldown() - System.currentTimeMillis())) / 1000).trim());
        }

        player.sendMessage(GRAY_LINE);
        // .... and that is how we do a /f who.
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Team) {
            return ((Team) obj).getName().equals(getName());
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