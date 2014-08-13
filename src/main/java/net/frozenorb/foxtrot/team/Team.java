package net.frozenorb.foxtrot.team;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.persist.KillsMap;
import net.frozenorb.foxtrot.raid.DTRHandler;
import net.frozenorb.foxtrot.server.ServerManager;
import net.frozenorb.foxtrot.team.claims.PhysicalChunk;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import redis.clients.jedis.Jedis;

@SuppressWarnings("deprecation")
public class Team {

	public static final int MAX_TEAM_SIZE = 30;

	private String name;

	private String owner = null;
	@Getter private Set<String> members = new HashSet<String>();
	@Getter private Set<String> captains = new HashSet<String>();

	private Location hq;

	private boolean friendlyFire;
	private boolean changed = false;
	private boolean loading = false;
	private String friendlyName;

	private String tag;

	@Getter private HashSet<String> invitations = new HashSet<String>();

	@Getter private Location rally;

	@Getter @Setter private BukkitRunnable runnable;

	@Getter private double dtr;
	@Getter @Setter private long rallyExpires;
	@Getter @Setter private long rallySetTime;

	private ArrayList<PhysicalChunk> chunks = new ArrayList<PhysicalChunk>();

	@Getter private long raidableCooldown;
	@Getter private long deathCooldown;

	@Getter private ArrayList<Subclaim> subclaims = new ArrayList<Subclaim>();

	public Team(String name) {
		this.name = name;
	}

	public void setDtr(double dtr) {
		this.dtr = dtr;
		setChanged(true);
	}

	public void setChanged(boolean hasChanged) {
		this.changed = hasChanged;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		changed = true;
		this.friendlyName = friendlyName;
	}

	public String getName() {
		return name;
	}

	public Location getHQ() {
		return hq;
	}

	public String getOwner() {
		return owner;
	}

	public void setTag(String tag) {
		changed = true;
		this.tag = tag;
	}

	public String getTag() {
		changed = true;
		return tag;
	}

	public void addMember(String member) {
		changed = true;
		members.add(member);

	}

	public void addCaptain(String captain) {
		changed = true;
		captains.add(captain);

	}

	public void removeCaptain(String name) {
		Iterator<String> iter = captains.iterator();

		while (iter.hasNext()) {
			if (iter.next().equalsIgnoreCase(name)) {
				iter.remove();
			}
		}
	}

	public void setOwner(String owner) {
		changed = true;
		this.owner = owner;
		members.add(owner);

	}

	public void setHQ(Location hq, boolean update) {
		changed = true;
		this.hq = hq;
	}

	public void setRally(Location rally, boolean update) {
		changed = true;
		this.rally = rally;
	}

	public void flagForSave() {
		changed = true;
	}

	public boolean isOwner(String name) {
		return owner.equalsIgnoreCase(name);
	}

	public String getActualPlayerName(String pName) {
		for (String str : members) {
			if (pName.equalsIgnoreCase(str))
				return str;
		}
		return null;
	}

	public boolean isFriendlyFire() {
		return friendlyFire;
	}

	public void setFriendlyFire(boolean friendlyFire) {
		changed = true;
		this.friendlyFire = friendlyFire;

	}

	public boolean isOnTeam(Player pl) {
		return isOnTeam(pl.getName());
	}

	public boolean isOnTeam(String name) {
		for (String member : members) {
			if (name.equalsIgnoreCase(member))
				return true;
		}

		return false;
	}

	public boolean isCaptain(String name) {
		for (String member : captains) {
			if (name.equalsIgnoreCase(member))
				return true;
		}

		return false;
	}

	public boolean remove(String name) {
		changed = true;

		for (Iterator<String> iterator = members.iterator(); iterator.hasNext();) {
			if (iterator.next().equalsIgnoreCase(name)) {
				iterator.remove();
				break;
			}
		}

		removeCaptain(name);

		if (isOwner(name)) {

			Iterator<String> iter = members.iterator();
			if (iter.hasNext()) {
				this.owner = members.iterator().next();
			}
		}

		Iterator<Subclaim> sc = subclaims.iterator();

		while (sc.hasNext()) {
			Subclaim s = sc.next();

			if (s.getManager().equalsIgnoreCase(name)) {
				sc.remove();
				continue;
			}

			if (s.isMember(name)) {
				s.removeMember(name);
			}
		}

		boolean emptyTeam = owner == null || members.size() == 0;

		if (!emptyTeam) {
			save();
		}

		if (dtr > getMaxDTR()) {
			dtr = getMaxDTR();
			changed = true;
		}

		return emptyTeam;
	}

	public boolean hasChanged() {
		return changed;
	}

	public int getOnlineMemberAmount() {
		int amt = 0;

		for (String m : getMembers()) {
			if (Bukkit.getPlayerExact(m) != null && !Bukkit.getPlayerExact(m).hasMetadata("invisible")) {
				amt++;
			}
		}
		return amt;

	}

	public ArrayList<Player> getOnlineMembers() {
		ArrayList<Player> pls = new ArrayList<Player>();

		for (String m : getMembers()) {
			if (Bukkit.getPlayerExact(m) != null && !Bukkit.getPlayerExact(m).hasMetadata("invisible")) {
				pls.add(Bukkit.getPlayerExact(m));
			}
		}
		return pls;

	}

	public ArrayList<String> getOfflineMembers() {
		ArrayList<String> pls = new ArrayList<String>();

		for (String m : getMembers()) {
			if (Bukkit.getPlayerExact(m) == null || Bukkit.getPlayerExact(m).hasMetadata("invisible")) {
				pls.add(m);
			}
		}
		return pls;

	}

	public Subclaim getSubclaim(String name, boolean fullName) {
		for (Subclaim sc : subclaims) {
			if ((!fullName && sc.getName().equalsIgnoreCase(name)) || sc.getFriendlyName().equalsIgnoreCase(name)) {
				return sc;
			}
		}
		return null;
	}

	public Subclaim getSubclaim(Location loc) {
		for (Subclaim sc : subclaims) {
			if (new CuboidRegion(sc.getName(), sc.getLoc1(), sc.getLoc2()).contains(loc)) {
				return sc;
			}
		}
		return null;
	}

	public int getMemberAmount() {
		return getMembers().size();
	}

	public int getSize() {
		return getMembers().size();
	}

	public boolean isRaidaible() {
		return dtr <= 0;
	}

	public void playerDeath(Player p) {
		playerDeath(p.getName());
	}

	public void playerDeath(String p) {
		setDtr(Math.max(dtr - 1D, -.99));

		if (isRaidaible()) {
			raidableCooldown = System.currentTimeMillis() + (7200 * 1000);

		}

		DTRHandler.setCooldown(this);
		deathCooldown = System.currentTimeMillis() + (3600 * 1000);

	}

	/**
	 * Gets the DTR increase that the team undergoes every minute
	 * 
	 * @return dtr increase per minute
	 */
	public BigDecimal getDTRIncrement() {
		double baseHour = DTRHandler.getBaseDTRIncrement(getSize());
		BigDecimal curr = new BigDecimal(0);

		ServerManager sm = FoxtrotPlugin.getInstance().getServerManager();

		for (Player p : getOnlineMembers()) {
			double mult = 0;

			Location loc = p.getLocation();

			if (sm.isWarzone(loc)) {
				mult = 1.25;
			}
			if (sm.isUnclaimed(loc)) {
				mult = 1.05;
			}
			if (FoxtrotPlugin.getInstance().getTeamManager().getOwner(new PhysicalChunk(loc.getChunk().getX(), loc.getChunk().getZ())) == this) {
				mult = 1;
			}

			curr = curr.add(new BigDecimal(baseHour + "").multiply(new BigDecimal(mult + "")));

		}

		return curr.divide(new BigDecimal(60 + ""), 5, RoundingMode.HALF_DOWN);
	}

	public double getMaxDTR() {
		int size = getSize();
		return Math.ceil((Math.log10(size / 5D) / Math.log10(2D)) + Math.round(size / 60D)) + 3;

	}

	public void load(String str) {
		loading = true;
		String[] lines = str.split("\n");
		for (String line : lines) {
			String identifier = line.substring(0, line.indexOf(':'));
			String[] lineParts = line.substring(line.indexOf(':') + 1).split(",");

			if (identifier.equalsIgnoreCase("Owner")) {
				setOwner(lineParts[0]);
			} else if (identifier.equalsIgnoreCase("Members")) {
				for (String name : lineParts) {
					if (name.length() >= 2) {
						addMember(name.trim());
					}
				}
			} else if (identifier.equalsIgnoreCase("Captains")) {

				for (String name : lineParts) {
					if (name.length() >= 2) {
						addCaptain(name.trim());
					}
				}

			} else if (identifier.equalsIgnoreCase("HQ")) {
				setHQ(parseLocation(lineParts), false);
			} else if (identifier.equalsIgnoreCase("DTR")) {
				setDtr(Double.parseDouble(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("Rally")) {
				setRally(parseLocation(lineParts), false);
			} else if (identifier.equalsIgnoreCase("FriendlyFire")) {
				setFriendlyFire(Boolean.parseBoolean(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("FriendlyName")) {
				setFriendlyName(lineParts[0]);
			} else if (identifier.equalsIgnoreCase("Chunks")) {
				for (String prt : lineParts) {
					prt = prt.replace("[", "").replace("]", "");
					if (prt.contains(":")) {
						int x = Integer.parseInt(prt.split(":")[0].trim());
						int z = Integer.parseInt(prt.split(":")[1].trim());
						getChunks().add(new PhysicalChunk(x, z));

					}
				}
			} else if (identifier.equalsIgnoreCase("Subclaims")) {
				for (String sc : lineParts) {
					if (!(sc.length() > 2)) {
						continue;
					}
					String[] part = sc.split("\\|");

					String loc1 = part[0];

					String world = loc1.split(" ")[0];
					double x = Double.parseDouble(loc1.split(" ")[1]);
					double y = Double.parseDouble(loc1.split(" ")[2]);
					double z = Double.parseDouble(loc1.split(" ")[3]);

					Location loc = new Location(Bukkit.getWorld(world), x, y, z);

					String loc2str = part[1];

					String world2 = loc2str.split(" ")[0];
					double x2 = Double.parseDouble(loc2str.split(" ")[1]);
					double y2 = Double.parseDouble(loc2str.split(" ")[2]);
					double z2 = Double.parseDouble(loc2str.split(" ")[3]);

					Location loc2 = new Location(Bukkit.getWorld(world2), x2, y2, z2);

					String manager = part[2];

					String name = part[3];

					Subclaim sclaim = new Subclaim(loc, loc2, manager, name);

					if (part.length > 4) {
						String members = part[4];

						for (String member : members.split(",")) {
							sclaim.addMember(member);
						}
					}

					subclaims.add(sclaim);

				}
			} else if (identifier.equalsIgnoreCase("Tag")) {
				tag = lineParts[0];
			}
		}
		loading = false;
		changed = false;
	}

	public void save(Jedis j) {

		changed = false;
		if (loading)
			return;
		StringBuilder teamString = new StringBuilder();
		String owners = owner;
		String members = "";
		String captains = "";

		Location homeLoc = getHQ();
		Location rally = getRally();

		boolean mFirst = true;
		for (String member : getMembers()) {
			if (!mFirst)
				members += ",";
			members += member;
			mFirst = false;
		}

		boolean cFirst = true;
		for (String captain : getCaptains()) {
			if (!cFirst)
				captains += ",";
			captains += captain;
			cFirst = false;
		}

		teamString.append("Owner:" + owners + '\n');
		teamString.append("Members:" + members + '\n');
		teamString.append("Captains:" + captains + '\n');
		teamString.append("DTR:" + dtr + '\n');

		if (homeLoc != null)
			teamString.append("HQ:" + homeLoc.getWorld().getName() + "," + homeLoc.getX() + "," + homeLoc.getY() + "," + homeLoc.getZ() + "," + homeLoc.getYaw() + "," + homeLoc.getPitch() + '\n');
		if (rally != null)
			teamString.append("Rally:" + rally.getWorld().getName() + "," + rally.getX() + "," + rally.getY() + "," + rally.getZ() + "," + rally.getYaw() + "," + rally.getPitch() + '\n');

		teamString.append("FriendlyFire:" + friendlyFire + '\n');
		teamString.append("FriendlyName:" + friendlyName + '\n');
		if (tag != null) {
			teamString.append("Tag:" + tag + '\n');
		}

		String scm = "";

		boolean first = true;
		for (Subclaim sc : subclaims) {
			if (!first) {
				scm += ",";
			}
			scm += sc.saveString();

			first = false;

		}

		teamString.append("Subclaims:" + scm + '\n');

		teamString.append("Chunks:" + chunks.toString() + '\n');
		j.set("fox_teams." + getName().toLowerCase(), teamString.toString());
		j.disconnect();
	}

	public int getMaxChunkAmount() {
		return /* Math.min(12, getMemberAmount() * 2) */20;
	}

	public ArrayList<PhysicalChunk> getChunks() {
		return chunks;
	}

	private Location parseLocation(String[] args) {
		if (args.length != 6)
			return null;

		World world = Bukkit.getWorld(args[0]);
		double x = Double.parseDouble(args[1]);
		double y = Double.parseDouble(args[2]);
		double z = Double.parseDouble(args[3]);
		float yaw = Float.parseFloat(args[4]);
		float pitch = Float.parseFloat(args[5]);

		return new Location(world, x, y, z, yaw, pitch);
	}

	public void sendTeamInfo(Player p) {
		String gray = "§7§m" + StringUtils.repeat("-", 53);

		p.sendMessage(gray);

		World w = Bukkit.getWorld("world");

		Location s = getHQ() != null ? getHQ() : getChunks().size() > 0 ? new Location(w, getChunks().get(0).getX() * 16, 70, getChunks().get(0).getZ() * 16) : null;

		String msg = " §3-§e HQ: ";
		msg += s != null ? "§f" + s.getBlockX() + ", " + s.getBlockZ() + "" : "§fNone";

		p.sendMessage("§9" + getFriendlyName() + " §7[" + getOnlineMemberAmount() + "/" + getSize() + "]" + msg);
		KillsMap km = FoxtrotPlugin.getInstance().getKillsMap();

		if (Bukkit.getPlayerExact(getOwner()) != null) {
			p.sendMessage("§eLeader: §a" + getOwner() + "§e[§a" + km.getKills(getOwner()) + "§e]");
		} else {
			p.sendMessage("§eLeader: §7" + getOwner() + "§e[§a" + km.getKills(getOwner()) + "§e]");
		}

		boolean first = true;
		boolean first2 = true;

		StringBuilder members = new StringBuilder("§eMembers: ");
		StringBuilder captains = new StringBuilder("§eCaptains: ");

		int captainAmount = 0;
		int memberAmount = 0;

		for (Player online : getOnlineMembers()) {
			StringBuilder toAdd = members;
			if (isOwner(online.getName())) {
				continue;
			}

			if (isCaptain(online.getName())) {

				toAdd = captains;
				if (!first2) {
					toAdd.append("§7, ");
				}
				captainAmount++;

				toAdd.append("§a" + online.getName() + "§e[§a" + km.getKills(online.getName()) + "§e]");
				first2 = false;
			} else {
				if (!first) {
					toAdd.append("§7, ");
				}
				memberAmount++;

				toAdd.append("§a" + online.getName() + "§e[§a" + km.getKills(online.getName()) + "§e]");
				first = false;
			}
		}
		for (String offline : getOfflineMembers()) {
			StringBuilder toAdd = members;

			if (isOwner(offline)) {
				continue;
			}
			if (isCaptain(offline)) {

				toAdd = captains;
				if (!first2) {
					toAdd.append("§7, ");
				}

				captainAmount++;

				toAdd.append("§7" + offline + "§e[§a" + km.getKills(offline) + "§e]");
				first2 = false;

			} else {
				if (!first) {
					toAdd.append("§7, ");
				}
				memberAmount++;

				toAdd.append("§7" + offline + "§e[§a" + km.getKills(offline) + "§e]");
				first = false;

			}
		}

		if (captainAmount > 0) {
			p.sendMessage(captains.toString());
		}

		if (memberAmount > 0) {
			p.sendMessage(members.toString());
		}
		if (isOnTeam(p)) {
			p.sendMessage("§eFriendly Fire: §7" + (isFriendlyFire() ? "On" : "Off"));
			p.sendMessage("§eClaimed Chunks: §7" + getChunks().size() + "/" + getMaxChunkAmount());
		}
		String dtrcolor = dtr / getMaxDTR() >= 0.25 ? "§a" : isRaidaible() ? "§4" : "§c";
		String dtrMsg = "§eDeaths Until Raidable: " + dtrcolor + new DecimalFormat("0.00").format(dtr);

		if (getOnlineMemberAmount() == 0) {
			dtrMsg += "§7■";
		} else {
			if (DTRHandler.isRegenerating(this)) {
				dtrMsg += "§a ▲";

			} else {
				if (DTRHandler.isOnCD(this)) {
					dtrMsg += "§c■";
				} else {
					dtrMsg += "§a■";

				}
			}
		}
		p.sendMessage(dtrMsg);

		p.sendMessage(gray);

	}

	public void save() {
		FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {
			@Override
			public Object execute(Jedis jedis) {
				save(jedis);
				return null;
			}
		});
	}

}
