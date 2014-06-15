package net.frozenorb.foxtrot.team;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.util.TimestampedLocation;
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

	@Getter @Setter private double dtr;

	private ArrayList<ClaimedChunk> chunks = new ArrayList<ClaimedChunk>();

	private HashMap<Location, Long> previousHomes = new HashMap<Location, Long>();

	public Team(String name) {
		this.name = name;
	}

	public ArrayList<TimestampedLocation> getPreviousHomes() {
		ArrayList<TimestampedLocation> tls = new ArrayList<TimestampedLocation>();
		for (Entry<Location, Long> entry : previousHomes.entrySet()) {
			tls.add(new TimestampedLocation(entry.getKey(), entry.getValue()));
		}
		Collections.sort(tls, new Comparator<TimestampedLocation>() {
			@Override
			public int compare(TimestampedLocation o1, TimestampedLocation o2) {
				return ((Long) o1.timestamp).compareTo(o2.timestamp);
			}
		});
		return tls;
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
		if (update) {
			previousHomes.put(hq, System.currentTimeMillis());
		}
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

		boolean emptyTeam = owner == null || members.size() == 0;

		if (!emptyTeam) {
			save();
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

	public int getMemberAmount() {
		return getMembers().size();
	}

	public int getSize() {
		return getMembers().size();
	}

	public boolean isRaidaible() {
		return dtr <= 0;
	}

	/**
	 * Gets the DTR increase that the team undergoes every hour after an hour.
	 * 
	 * @return dtr increase per hour
	 */
	public double getDTRIncrement() {
		return 0.5;
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
			} else if (identifier.equalsIgnoreCase("Rally")) {
				setRally(parseLocation(lineParts), false);
			} else if (identifier.equalsIgnoreCase("FriendlyFire")) {
				setFriendlyFire(Boolean.parseBoolean(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("FriendlyName")) {
				setFriendlyName(lineParts[0]);
			} else if (identifier.equalsIgnoreCase("PreviousHomes")) {

				for (String meta : lineParts) {
					if (!meta.contains(" - "))
						continue;
					long timestamp = Long.parseLong(meta.split(" - ")[1]);
					String coords = meta.split(" - ")[0].replace("(", "").replace(")", "");
					double x = Double.parseDouble(coords.split(" ")[0]);
					double y = Double.parseDouble(coords.split(" ")[1]);
					double z = Double.parseDouble(coords.split(" ")[2]);
					float n = Float.parseFloat(coords.split(" ")[3]);
					float n2 = Float.parseFloat(coords.split(" ")[4]);
					String world = coords.split(" ")[5];
					Location loc = new Location(Bukkit.getWorld(world), x, y, z, n, n2);
					previousHomes.put(loc, timestamp);

				}

			} else if (identifier.equalsIgnoreCase("Chunks")) {
				for (String prt : lineParts) {
					prt = prt.replace("[", "").replace("]", "");
					if (prt.contains(":")) {
						int x = Integer.parseInt(prt.split(":")[0].trim());
						int z = Integer.parseInt(prt.split(":")[1].trim());
						getChunks().add(new ClaimedChunk(x, z));
						System.out.println("loaded:" + x + " <-> " + z + "\n\n\n\n\n\n");

					}
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

		String prevHomes = "";
		boolean hqFirst = true;
		for (Entry<Location, Long> entry : previousHomes.entrySet()) {
			if (!hqFirst)
				prevHomes += ",";
			hqFirst = false;
			Location l = entry.getKey();
			prevHomes += ("(" + l.getX() + " " + l.getY() + " " + l.getZ() + " " + l.getYaw() + " " + l.getPitch() + " " + l.getWorld().getName() + ") - " + entry.getValue());
		}

		teamString.append("Owner:" + owners + '\n');
		teamString.append("Members:" + members + '\n');
		teamString.append("Captains:" + captains + '\n');

		if (homeLoc != null)
			teamString.append("HQ:" + homeLoc.getWorld().getName() + "," + homeLoc.getX() + "," + homeLoc.getY() + "," + homeLoc.getZ() + "," + homeLoc.getYaw() + "," + homeLoc.getPitch() + '\n');
		if (rally != null)
			teamString.append("Rally:" + rally.getWorld().getName() + "," + rally.getX() + "," + rally.getY() + "," + rally.getZ() + "," + rally.getYaw() + "," + rally.getPitch() + '\n');

		teamString.append("FriendlyFire:" + friendlyFire + '\n');
		teamString.append("FriendlyName:" + friendlyName + '\n');
		teamString.append("PreviousHomes:" + prevHomes + '\n');
		if (tag != null) {
			teamString.append("Tag:" + tag + '\n');
		}

		teamString.append("Chunks:" + chunks.toString() + '\n');
		j.set("fox_teams." + getName().toLowerCase(), teamString.toString());
		j.disconnect();
	}

	public int getMaxChunkAmount() {
		return /* Math.min(12, getMemberAmount() * 2) */20;
	}

	public ArrayList<ClaimedChunk> getChunks() {
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
		msg += s != null ? "§7" + s.getBlockX() + ", " + s.getBlockZ() + "" : "§7None";

		p.sendMessage("§9" + getFriendlyName() + " §7[" + getOnlineMemberAmount() + "/" + getSize() + "]" + msg);

		if (Bukkit.getPlayerExact(getOwner()) != null) {
			p.sendMessage("§eLeader: §a" + getOwner());
		} else {
			p.sendMessage("§eLeader: §7" + getOwner());
		}

		boolean first = true;
		boolean first2 = true;

		StringBuilder strb = new StringBuilder("§eMembers: ");
		StringBuilder strb2 = new StringBuilder("§eCaptains: ");

		for (Player online : getOnlineMembers()) {
			StringBuilder toAdd = strb;
			if (isOwner(online.getName())) {
				continue;
			}

			if (isCaptain(online.getName())) {

				toAdd = strb2;
				if (!first2) {
					toAdd.append("§7, ");
				}

				toAdd.append("§a" + online.getName());
				first2 = false;
			} else {
				if (!first) {
					toAdd.append("§7, ");
				}

				toAdd.append("§a" + online.getName());
				first = false;
			}
		}
		for (String offline : getOfflineMembers()) {
			StringBuilder toAdd = strb;

			if (isOwner(offline)) {
				continue;
			}
			if (isCaptain(offline)) {

				toAdd = strb2;
				if (!first2) {
					toAdd.append("§7, ");
				}

				toAdd.append("§7" + offline);
				first2 = false;

			} else {
				if (!first) {
					toAdd.append("§7, ");
				}
				toAdd.append("§7" + offline);
				first = false;
			}
		}

		p.sendMessage(strb2.toString());
		p.sendMessage(strb.toString());

		if (isOnTeam(p)) {
			p.sendMessage("§eFriendly Fire: §7" + (isFriendlyFire() ? "On" : "Off"));
			p.sendMessage("§eClaimed Chunks: §7" + getChunks().size() + "/" + getMaxChunkAmount());
		}
		String dtrcolor = dtr / getMaxDTR() >= 0.25 ? "§a" : isRaidaible() ? "§4" : "§c";
		String dtrMsg = "§eDeaths Until Raidable: " + dtrcolor + new DecimalFormat("0.00").format(dtr);
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
