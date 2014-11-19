package net.frozenorb.foxtrot.team;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.persist.KillsMap;
import net.frozenorb.foxtrot.raid.DTRHandler;
import net.frozenorb.foxtrot.team.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.mBasic.Basic;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Team {

    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");

    // Configurable values //

	public static final int MAX_TEAM_SIZE = 30;
    public static final int MAX_CLAIMS = 2;
    public static final long DTR_REGEN_TIME = TimeUnit.MINUTES.toMillis(60);
    public static final long RAIDABLE_REGEN_TIME = TimeUnit.MINUTES.toMillis(90);

    // End configurable values //

	@Getter @Setter private String name;
    @Getter private String friendlyName;
    @Getter private Location hq;

	@Getter private String owner = null;
	@Getter private Set<String> members = new HashSet<String>();
	@Getter private Set<String> captains = new HashSet<String>();

	@Getter private boolean needsSave = false;
	@Getter private boolean loading = false;

	@Getter private Set<String> invitations = new HashSet<String>();
	@Getter private double dtr;

	@Getter private List<Claim> claims = new ArrayList<Claim>();

	@Getter private long raidableCooldown;
	@Getter private long deathCooldown;

	@Getter private double balance;

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
            flagForSave();
        }
	}

	public void setFriendlyName(String friendlyName) {
		flagForSave();
		this.friendlyName = friendlyName;
	}

	public void addMember(String member) {
        if (!member.equalsIgnoreCase("null")) {
            flagForSave();
            members.add(member);
            FactionActionTracker.logAction(this, "actions", "Member Added: " + member);
        }
	}

	public void addCaptain(String captain) {
		flagForSave();
		captains.add(captain);
        FactionActionTracker.logAction(this, "actions", "Captain Added: " + captain);
	}

    public void setBalance(double balance) {
        flagForSave();
        this.balance = balance;
    }

    public void setRaidableCooldown(long raidableCooldown) {
        flagForSave();
        this.raidableCooldown = raidableCooldown;
    }

    public void setDeathCooldown(long deathCooldown) {
        flagForSave();
        this.deathCooldown = deathCooldown;
    }

	public void removeCaptain(String name) {
        FactionActionTracker.logAction(this, "actions", "Captain Removed: " + name);
        Iterator<String> iterator = captains.iterator();

		while (iterator.hasNext()) {
			if (iterator.next().equalsIgnoreCase(name)) {
                iterator.remove();
			}
		}
	}

	public void setOwner(String owner) {
		flagForSave();
        FactionActionTracker.logAction(this, "actions", "Owner Changed: " + this.owner + " -> " + owner);
		this.owner = owner;

        if (owner != null && !owner.equals("null")) {
            members.add(owner);
        }
	}

	public void setHQ(Location hq) {
        String oldHQ = this.hq == null ? "None" : (this.hq.getBlockX() + ", " + this.hq.getBlockY() + ", " + this.hq.getBlockZ());
        String newHQ = hq == null ? "None" : (hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ());
        FactionActionTracker.logAction(this, "actions", "HQ Changed: [" + oldHQ + "] -> [" + newHQ + "]");
		flagForSave();
		this.hq = hq;
	}

    public void disband() {
        Basic.get().getEconomyManager().depositPlayer(owner, balance);

        for (String member : members) {
            FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeamMap().remove(member.toLowerCase());
        }

        LandBoard.getInstance().clear(this);
        FoxtrotPlugin.getInstance().getTeamHandler().getTeamNameMap().remove(name.toLowerCase());

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
        final String oldName = friendlyName;

        this.name = newName.toLowerCase();
        this.friendlyName = newName;

        for (Claim claim : claims) {
            claim.setName(claim.getName().replaceAll(oldName, newName));
        }

        FoxtrotPlugin.getInstance().getTeamHandler().getTeamNameMap().remove(oldName.toLowerCase());
        // .addTeam handles updating the player-team cache.
        FoxtrotPlugin.getInstance().getTeamHandler().addTeam(this);

        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

            public Object execute(Jedis jedis) {
                jedis.del("fox_teams." + oldName.toLowerCase());
                return (null);
            }

        });
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

			if (s.isMember(name)) {
				s.removeMember(name);
			}
		}

        // Is this needed?
		boolean emptyTeam = owner == null || members.size() == 0;

		if (dtr > getMaxDTR()) {
			dtr = getMaxDTR();
		}

        flagForSave();
		return (emptyTeam);
	}

    public boolean hasDTRBitmask(DTRBitmaskType bitmaskType) {
        if (getOwner() != null) {
            return (false);
        }

        int dtrInt = (int) dtr;
        return (((dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask()));
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

	public Subclaim getSubclaim(String name) {
		for (Subclaim sc : subclaims) {
			if (sc.getName().equalsIgnoreCase(name)) {
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
                if (!lineParts[0].equals("null")) {
                    setOwner(lineParts[0]);
                }
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
                        String world = split[7].trim();

						Claim claimObj = new Claim(world, x1, y1, z1, x2, y2, z2);
						claimObj.setName(name);

						getClaims().add(claimObj);
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
                        String members = split[7].trim();

                        Location loc1 = new Location(FoxtrotPlugin.getInstance().getServer().getWorld("world"), x1, y1, z1);
                        Location loc2 = new Location(FoxtrotPlugin.getInstance().getServer().getWorld("world"), x2, y2, z2);

                        Subclaim subclaimObj = new Subclaim(loc1, loc2, name);
                        subclaimObj.setMembers(new ArrayList<String>(Arrays.asList(members.split(","))));

                        getSubclaims().add(subclaimObj);
                    }
                }
			}
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
		Location homeLoc = getHq();

		for (String member : getMembers()) {
			members.append(member.trim()).append(", ");
		}

        if (members.length() > 2) {
            members.setLength(members.length() - 2);
        }

		for (String captain : getCaptains()) {
            captains.append(captain.trim()).append(", ");
        }

        if (captains.length() > 2) {
            captains.setLength(captains.length() - 2);
        }

        for (String invite : getInvitations()) {
            invites.append(invite.trim()).append(", ");
        }

        if (invites.length() > 2) {
            invites.setLength(invites.length() - 2);
        }

		teamString.append("Owner:").append(getOwner()).append('\n');
        teamString.append("Captains:").append(captains.toString()).append('\n');
		teamString.append("Members:").append(members.toString()).append('\n');
        teamString.append("Invited:").append(invites.toString()).append('\n');
        teamString.append("Subclaims:").append(getSubclaims().toString()).append('\n');
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
            Location hq = getHq();
            String hqString = hq != null ? "§f" + hq.getBlockX() + ", " + hq.getBlockZ() + "" : "§fNone";

            player.sendMessage("§9" + getFriendlyName() + " §7[" + getOnlineMemberAmount() + "/" + getSize() + "]  §3-§e HQ: " + hqString);
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
        } else {
            String hqString = hq != null ? "§f" + hq.getBlockX() + ", " + hq.getBlockZ() + "" : "§fNone";

            if (hasDTRBitmask(DTRBitmaskType.KOTH)) {
                player.sendMessage(ChatColor.AQUA + getFriendlyName() + ChatColor.GOLD + " KOTH");
            } else if (hasDTRBitmask(DTRBitmaskType.CITADEL_TOWN)) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Citadel Town");
            }  else if (hasDTRBitmask(DTRBitmaskType.CITADEL_COURTYARD)) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Citadel Courtyard");
            }  else if (hasDTRBitmask(DTRBitmaskType.CITADEL_KEEP)) {
                player.sendMessage(ChatColor.DARK_PURPLE + "Citadel Keep");
            } else {
                player.sendMessage(ChatColor.BLUE + getFriendlyName());
            }

            player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + hqString);

            if (player.isOp() && player.getItemInHand() != null && player.getItemInHand().getType() == Material.REDSTONE_BLOCK) {
                player.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "Info: " + getDtr() + " DTR Bitmask");
            }
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