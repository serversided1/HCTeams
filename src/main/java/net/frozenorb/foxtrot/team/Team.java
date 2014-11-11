package net.frozenorb.foxtrot.team;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.jedis.persist.KillsMap;
import net.frozenorb.foxtrot.raid.DTRHandler;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Team {

    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");

    // Configurable values //

	public static final int MAX_TEAM_SIZE = 30;
    public static final long DTR_REGEN_TIME = TimeUnit.MINUTES.toMillis(90);
    public static final long RAIDABLE_REGEN_TIME = TimeUnit.MINUTES.toMillis(120);
    public static final int MAX_CLAIMS = 2;

    // End configurable values //

	@Getter @Setter private String name;
    @Getter private String friendlyName;
    @Getter private Location hq;

	@Getter private String owner = null;
	@Getter private Set<String> members = new HashSet<String>();
	@Getter private Set<String> captains = new HashSet<String>();

	@Getter @Setter private boolean changed = false;
	@Getter private boolean loading = false;

	@Getter private Set<String> invitations = new HashSet<String>();
	@Getter private double dtr;

	@Getter private List<Claim> claims = new ArrayList<Claim>();

	@Getter @Setter private long raidableCooldown;
	@Getter @Setter private long deathCooldown;

	@Getter @Setter private double balance;

	@Getter private List<Subclaim> subclaims = new ArrayList<Subclaim>();

	public Team(String name) {
		this.name = name;
	}

	public void setDtr(double newDTR) {
        if (dtr != newDTR) {
            if (dtr <= 0 && newDTR > 0) {
                FactionActionTracker.logAction(this, "actions", "Faction no longer raidable.");
            }

            if (Math.abs(newDTR - dtr) > 0.4) {
                FactionActionTracker.logAction(this, "actions", "DTR Change: More than 0.4 [Old DTR: " + dtr + ", New DTR: " + newDTR + "]");
            }

            if (!isLoading()) {
                FoxtrotPlugin.getInstance().getLogger().info("[DTR Change] Team: " + name + " > " + "Old DTR: [" + dtr + "] | New DTR: [" + newDTR + "] | DTR Diff: [" + (dtr - newDTR) + "]");
            }

            this.dtr = newDTR;
            setChanged(true);
        }
	}

	public void setFriendlyName(String friendlyName) {
		changed = true;
		this.friendlyName = friendlyName;
	}

	public void addMember(String member) {
		changed = true;
		members.add(member);
        FactionActionTracker.logAction(this, "actions", "Member Added: " + member);
	}

	public void addCaptain(String captain) {
		changed = true;
		captains.add(captain);
        FactionActionTracker.logAction(this, "actions", "Captain Added: " + captain);
	}

	public void removeCaptain(String name) {
		Iterator<String> iter = captains.iterator();
        FactionActionTracker.logAction(this, "actions", "Captain Removed: " + name);

		while (iter.hasNext()) {
			if (iter.next().equalsIgnoreCase(name)) {
				iter.remove();
			}
		}
	}

	public void setOwner(String owner) {
		changed = true;
        FactionActionTracker.logAction(this, "actions", "Owner Changed: " + this.owner + " -> " + owner);
		this.owner = owner;

        if (owner != null) {
            members.add(owner);
        }
	}

	public void setHQ(Location hq) {
        String oldHQ = this.hq == null ? "None" : (this.hq.getBlockX() + ", " + this.hq.getBlockY() + ", " + this.hq.getBlockZ());
        String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
        FactionActionTracker.logAction(this, "actions", "HQ Changed: [" + oldHQ + "] -> [" + newHQ + "]");
		changed = true;
		this.hq = hq;
	}

	public void flagForSave() {
		changed = true;
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
        //TODO
        return (false);
    }

	public boolean ownsLocation(Location loc) {
		return (FoxtrotPlugin.getInstance().getTeamHandler().getOwner(loc) == this);
	}

	public boolean ownsClaim(Claim cc) {
		return (FoxtrotPlugin.getInstance().getTeamHandler().getOwner(cc) == this);
	}

	public boolean removeMember(String name) {
		changed = true;
        FactionActionTracker.logAction(this, "actions", "Member Removed: " + name);

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

        // Is this needed?
		boolean emptyTeam = owner == null || members.size() == 0;

		if (dtr > getMaxDTR()) {
			dtr = getMaxDTR();
			changed = true;
		}

		return (emptyTeam);
	}

	public int getOnlineMemberAmount() {
		int amt = 0;

		for (String m : getMembers()) {
            Player exactPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(m);

			if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
				amt++;
			}
		}

		return (amt);
	}

	public List<Player> getOnlineMembers() {
		List<Player> players = new ArrayList<Player>();

		for (String m : getMembers()) {
            if (m == null) {
                continue;
            }

            Player exactPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(m);

			if (exactPlayer != null && !exactPlayer.hasMetadata("invisible")) {
				players.add(exactPlayer);
			}
		}

		return (players);
	}

	public List<String> getOfflineMembers() {
		List<String> players = new ArrayList<String>();

		for (String m : getMembers()) {
            if (m == null) {
                continue;
            }

            Player exactPlayer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(m);

			if (exactPlayer == null || exactPlayer.hasMetadata("invisible")) {
				players.add(m);
			}
		}

		return (players);
	}

	public Subclaim getSubclaim(String name, boolean fullName) {
		for (Subclaim sc : subclaims) {
			if ((!fullName && sc.getName().equalsIgnoreCase(name)) || sc.getFriendlyName().equalsIgnoreCase(name)) {
				return (sc);
			}
		}

		return (null);
	}

	public Subclaim getSubclaim(Location loc) {
		for (Subclaim sc : subclaims) {
			if (new CuboidRegion(sc.getName(), sc.getLoc1(), sc.getLoc2()).contains(loc)) {
				return (sc);
			}
		}

		return (null);
	}

	public int getSize() {
		return (getMembers().size());
	}

	public boolean isRaidable() {
        // If their DTR is 0, they ARE raidable.
		return (dtr <= 0);
	}

	public void playerDeath(String p, double dtrLoss) {
        double newDTR = Math.max(dtr - dtrLoss, -.99);
        FactionActionTracker.logAction(this, "actions", "Member Death: " + p + " [DTR Loss: " + dtrLoss + ", Old DTR: " + dtr + ", New DTR: " + newDTR + "]");

        for (Player player : getOnlineMembers()) {
            player.sendMessage(ChatColor.RED + "Member Death: " + ChatColor.WHITE + p);
            player.sendMessage(ChatColor.RED + "DTR: " + ChatColor.WHITE + DTR_FORMAT.format(newDTR));
        }

        FoxtrotPlugin.getInstance().getLogger().info("[TeamDeath] " + name + " > " + "Player death: [" + p + "]");
        setDtr(newDTR);

		if (isRaidable()) {
            FactionActionTracker.logAction(this, "actions", "Faction now raidable.");
			raidableCooldown = System.currentTimeMillis() + RAIDABLE_REGEN_TIME;
		}

		DTRHandler.setCooldown(this);
		deathCooldown = System.currentTimeMillis() + DTR_REGEN_TIME;
	}

	public BigDecimal getDTRIncrement() {
		return (getDTRIncrement(getOnlineMemberAmount()));
	}

    // I'm not quite sure why we're using BigDecimals here.
    public BigDecimal getDTRIncrement(int playersOnline) {
        BigDecimal dtrPerHour = new BigDecimal(DTRHandler.getBaseDTRIncrement(getSize())).multiply(new BigDecimal(playersOnline));
        return (dtrPerHour.divide(new BigDecimal(60 + ""), 5, RoundingMode.HALF_DOWN));
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
				setOwner(lineParts[0]);
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
				setDtr(Double.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("Balance")) {
				setBalance(Double.valueOf(lineParts[0]));
			} else if (identifier.equalsIgnoreCase("DeathCooldown")) {
                setDeathCooldown(Long.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("RaidableCooldown")) {
                setRaidableCooldown(Long.valueOf(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FriendlyName")) {
				setFriendlyName(lineParts[0]);
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

						Claim claimObj = new Claim(x1, y1, z1, x2, y2, z2);
						claimObj.setName(name);

						getClaims().add(claimObj);
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
			}
		}

		loading = false;
		changed = false;
	}

	public String saveString() {
		changed = false;

		if (loading) {
            return (null);
        }

		StringBuilder teamString = new StringBuilder();

		StringBuilder members = new StringBuilder();
		StringBuilder captains = new StringBuilder();
        StringBuilder invites = new StringBuilder();
        StringBuilder subclaims = new StringBuilder();
		Location homeLoc = getHq();

		for (String member : getMembers()) {
			members.append(member).append(", ");
		}

        if (members.length() > 2) {
            members.setLength(members.length() - 2);
        }

		for (String captain : getCaptains()) {
            captains.append(captain).append(", ");
        }

        if (captains.length() > 2) {
            captains.setLength(captains.length() - 2);
        }

        for (String invite : getInvitations()) {
            invites.append(invite).append(", ");
        }

        if (invites.length() > 2) {
            invites.setLength(invites.length() - 2);
        }

        for (Subclaim subclaim : getSubclaims()) {
            subclaims.append(subclaim.saveString()).append(", ");
        }

        if (subclaims.length() > 2) {
            subclaims.setLength(subclaims.length() - 2);
        }

		teamString.append("Owner:").append(getOwner()).append('\n');
        teamString.append("Captains:").append(captains.toString()).append('\n');
		teamString.append("Members:").append(members.toString()).append('\n');
        teamString.append("Invited:").append(invites.toString()).append('\n');
        teamString.append("Subclaims:").append(subclaims.toString()).append('\n');
        teamString.append("Claims:").append(getClaims().toString()).append('\n');
		teamString.append("DTR:").append(getDtr()).append('\n');
		teamString.append("Balance:").append(getBalance()).append('\n');
        teamString.append("DeathCooldown:").append(getDeathCooldown()).append('\n');
        teamString.append("RaidableCooldown:").append(getRaidableCooldown()).append('\n');
        teamString.append("FriendlyName:").append(getFriendlyName()).append('\n');

		if (homeLoc != null) {
            teamString.append("HQ:").append(homeLoc.getWorld().getName()).append(",").append(homeLoc.getX()).append(",").append(homeLoc.getY()).append(",").append(homeLoc.getZ()).append(",").append(homeLoc.getYaw()).append(",").append(homeLoc.getPitch()).append('\n');
        }

        return (teamString.toString());
	}

	public int getMaxClaimAmount() {
		return (MAX_CLAIMS);
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
        String gray = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);

        player.sendMessage(gray);

        // A string comparison is needed here as saving and reload the data results in a string known as 'null', not a null value.
        if (owner != null && !owner.equalsIgnoreCase("null")) {
            Location s = getHq();

            String msg = " §3-§e HQ: ";
            msg += s != null ? "§f" + s.getBlockX() + ", " + s.getBlockZ() + "" : "§fNone";

            player.sendMessage("§9" + getFriendlyName() + " §7[" + getOnlineMemberAmount() + "/" + getSize() + "]" + msg);
            KillsMap km = FoxtrotPlugin.getInstance().getKillsMap();
            Player owner = Bukkit.getPlayerExact(getOwner());

            if (owner != null && !owner.hasMetadata("invisible")) {
                player.sendMessage("§eLeader: §a" + getOwner() + "§e[§a" + km.getKills(getOwner()) + "§e]");
            } else {
                player.sendMessage("§eLeader: §7" + getOwner() + "§e[§a" + km.getKills(getOwner()) + "§e]");
            }

            boolean first = true;
            boolean first2 = true;

            StringBuilder members = new StringBuilder("§eMembers: ");
            StringBuilder captains = new StringBuilder("§eCaptains: ");

            int captainAmount = 0;
            int memberAmount = 0;

            for (Player online : getOnlineMembers()) {
                if (online.hasMetadata("invisible")) {
                    continue;
                }

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
                    } else {
                        first2 = false;
                    }

                    captainAmount++;
                    toAdd.append("§7" + offline + "§e[§a" + km.getKills(offline) + "§e]");
                } else {
                    if (!first) {
                        toAdd.append("§7, ");
                    } else {
                        first = false;
                    }

                    memberAmount++;
                    toAdd.append("§7" + offline + "§e[§a" + km.getKills(offline) + "§e]");
                }
            }

            if (captainAmount > 0) {
                player.sendMessage(captains.toString());
            }

            if (memberAmount > 0) {
                player.sendMessage(members.toString());
            }

            // Round the Team's balance.
            player.sendMessage(ChatColor.YELLOW + "Balance: " + ChatColor.BLUE + "$" + Math.round(balance));

            // Get a Team's DTR color.
            ChatColor dtrColor = ChatColor.GREEN;

            if (dtr / getMaxDTR() <= 0.25) {
                if (isRaidable()) {
                    dtrColor = ChatColor.DARK_RED;
                } else {
                    dtrColor = ChatColor.YELLOW;
                }
            }

            String dtrMsg = ChatColor.YELLOW + "Deaths Until Raidable: " + dtrColor + DTR_FORMAT.format(dtr);
            boolean showTimeUntilRegen = false;

            if (getOnlineMemberAmount() == 0) {
                // No players online.
                dtrMsg += ChatColor.GRAY + "◀";
            } else {
                if (DTRHandler.isRegenerating(this)) {
                    // Regenerating
                    dtrMsg += ChatColor.GREEN + "▲";
                } else {
                    if (DTRHandler.isOnCD(this)) {
                        // On cooldown
                        dtrMsg += ChatColor.RED + "■";
                        showTimeUntilRegen = true;
                    } else {
                        dtrMsg += ChatColor.GREEN + "◀";
                    }
                }
            }

            player.sendMessage(dtrMsg);

            if (showTimeUntilRegen) {
                long till = Math.max(getRaidableCooldown(), getDeathCooldown());
                int seconds = ((int) (till - System.currentTimeMillis())) / 1000;
                player.sendMessage(ChatColor.YELLOW + "Time Until Regen: " + ChatColor.BLUE + TimeUtils.getConvertedTime(seconds).trim());
            }
        } else if (getDtr() == 50D) {
            player.sendMessage(ChatColor.AQUA + getFriendlyName() + ChatColor.GOLD + " KOTH " + ChatColor.GRAY + "(5m Deathban)");
            player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (getHq() == null ? "None" : getHq().getBlockX() + ", " + getHq().getBlockZ()));
        } else if (getDtr() == 100D) {
            player.sendMessage(ChatColor.DARK_PURPLE + getFriendlyName() + ChatColor.WHITE + " " + ChatColor.GRAY + "(15m Deathban, 0.5 DTR Loss, 60s Pearl Cooldown)");
            player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (getHq() == null ? "None" : getHq().getBlockX() + ", " + getHq().getBlockZ()));
        } else {
            player.sendMessage(ChatColor.BLUE + getFriendlyName());
            player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (getHq() == null ? "None" : getHq().getBlockX() + ", " + getHq().getBlockZ()));
        }

		player.sendMessage(gray);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Team) {
			return ((Team) obj).getName().equals(getName());
		}

		return (super.equals(obj));
	}

}