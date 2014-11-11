package net.frozenorb.foxtrot.team.claims;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim.CuboidDirection;
import net.frozenorb.mBasic.Utilities.ItemDb;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class VisualClaim implements Listener {

	public static final int MAP_RADIUS = 50;
	public static final Material[] MAP_MATERIALS = { Material.DIAMOND_BLOCK,
			Material.GOLD_BLOCK, Material.LOG, Material.BRICK, Material.WOOD,
			Material.REDSTONE_BLOCK, Material.LAPIS_BLOCK, Material.CHEST,
			Material.MELON_BLOCK, Material.STONE, Material.COBBLESTONE,
			Material.COAL_BLOCK, Material.DIAMOND_ORE, Material.COAL_ORE,
			Material.GOLD_ORE, Material.REDSTONE_ORE, Material.FURNACE };

	@Getter private static Map<String, VisualClaim> currentMaps = new HashMap<String, VisualClaim>();
	@Getter private static Map<String, VisualClaim> visualClaims = new HashMap<String, VisualClaim>();
	private static Map<String, List<Location>> packetBlocksSent = new HashMap<String, List<Location>>();
	private static Map<String, List<Location>> mapBlocksSent = new HashMap<String, List<Location>>();

	@Getter @NonNull private Player player;
	@NonNull private VisualType type;
    @NonNull private boolean bypass;

	private Location corner1;
	private Location corner2;

	public void draw(boolean silent) {
        // If they already have a
		if (currentMaps.containsKey(player.getName()) && type == VisualType.MAP) {
			currentMaps.get(player.getName()).cancel(true);

			if (!silent) {
				player.sendMessage(ChatColor.YELLOW + "Claim pillars have been hidden!");
			}

			return;
		}

		if (visualClaims.containsKey(player.getName()) && type != VisualType.MAP) {
			visualClaims.get(player.getName()).cancel(true);
		}

		if (type == VisualType.CREATE || type == VisualType.RESIZE) {
			visualClaims.put(player.getName(), this);
		} else {
			currentMaps.put(player.getName(), this);
		}

		Bukkit.getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());

		int x = player.getLocation().getBlockX();
        int z = player.getLocation().getBlockZ();

		if (type == VisualType.MAP) {
			int iter = 0;

			HashMap<Team, Material> storageReference = new HashMap<Team, Material>();
			HashMap<Claim, Material> sendMaps = new HashMap<Claim, Material>();

			for (Claim c : LandBoard.getInstance().getClaims()) {
				if (c.isWithin(x, z, MAP_RADIUS)) {
					Team owner = LandBoard.getInstance().getTeamAt(c);
					Material mat = getMaterial(iter);

					if (storageReference.containsKey(owner)) {
						mat = storageReference.get(owner);
					} else {
						iter++;
					}

					drawPillars(c, mat);
					sendMaps.put(c, mat);
					storageReference.put(owner, mat);
				}
			}

			Claim spawn = new Claim(RegionManager.get().getByName("spawn").getMinimumPoint(), RegionManager.get().getByName("spawn").getMaximumPoint());
			spawn.setName("Spawn");

			if (spawn.isWithin(x, z, MAP_RADIUS)) {
				Material mat = getMaterial(-1);

				drawPillars(spawn, mat);
				sendMaps.put(spawn, mat);
			}

			if (sendMaps.isEmpty()) {
				player.sendMessage(ChatColor.YELLOW + "There are no claims within " + MAP_RADIUS + " blocks of you!");
				cancel(true);
			}

			if (!silent) {
				sendMaps.forEach((c, m) -> {
					if (c.getName() != null && c.getName().equals("Spawn")) {
						player.sendMessage("§eLand §9Spawn§a(§b" + ItemDb.getFriendlyName(new ItemStack(m)) + "§a) §eis claimed by §9Spawn");
					} else {
						player.sendMessage("§eLand §9" + c.getName() + "§a(§b" + ItemDb.getFriendlyName(new ItemStack(m)) + "§a) §eis claimed by §9" + LandBoard.getInstance().getTeamAt(c).getFriendlyName());
					}
				});
			}
		}
	}

	public boolean containsOtherClaim(Claim c) {
		CuboidRegion cr = new CuboidRegion("", c.getMinimumPoint(), c.getMaximumPoint());
		boolean claimed = false;

		if (!FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(cr.getMaximumPoint())) {
			claimed = true;
		}

		if (!FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(cr.getMinimumPoint())) {
			claimed = true;
		}

		for (Location l : cr) {
            if (!FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(l)) {
                claimed = true;
            }
        }

		return (claimed);
	}

	public HashSet<Claim> touchesOtherClaim(Claim c) {
		HashSet<Claim> touchingClaims = new HashSet<Claim>();

		c.outset(CuboidDirection.Horizontal, 1).forEach(cr -> {
			Location loc = new Location(Bukkit.getWorld("world"), cr.getX(), 80, cr.getZ());

			Claim cc = LandBoard.getInstance().getClaimAt(loc);

			if (cc != null) {
				touchingClaims.add(cc);
			}
		});

		return touchingClaims;
	}

	public void setLoc(int loc, Location to) {

		if (!FoxtrotPlugin.getInstance().getTeamHandler().isOnTeam(player.getName())) {
			player.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
			cancel(true);
			return;
		}

		Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

		if (loc == 1) {

			if (corner2 != null) {
				Claim check = new Claim(to, corner2);

				if (!bypass && containsOtherClaim(check)) {
					player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
					return;
				}

				HashSet<Claim> touching = touchesOtherClaim(check);
				HashSet<Claim> cloneCheck = new HashSet<Claim>();
				touching.forEach(tee -> cloneCheck.add(tee.clone()));

				boolean contains = cloneCheck.removeIf(c -> t.ownsClaim(c));
				if (t.getClaims().size() > 0 && !contains) {
					player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
					return;
				}

				if (touching.size() > 1 || (touching.size() == 1 && !contains)) {
					player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
					return;
				}

				int x = Math.abs(check.x1 - check.x2);
				int z = Math.abs(check.z1 - check.z2);

				if (x < 4 || z < 4) {
					player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least (§f5 x 5§c)!");
					return;
				}

				if (x >= 3 * z || z >= 3 * x) {
					player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
					return;
				}

			}

			clearPillarAt(corner1);
			this.corner1 = to;

		} else if (loc == 2) {

			if (corner1 != null) {
				Claim check = new Claim(corner1, to);

				if (!bypass && containsOtherClaim(check)) {
					player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
					return;
				}

				HashSet<Claim> touching = touchesOtherClaim(check);

				HashSet<Claim> cloneCheck = new HashSet<Claim>();
				touching.forEach(tee -> cloneCheck.add(tee.clone()));

				boolean contains = cloneCheck.removeIf(c -> t.ownsClaim(c));
				if (t.getClaims().size() > 0 && !contains) {
					player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
					return;
				}

				if (touching.size() > 1 || (touching.size() == 1 && !contains)) {
					player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
					return;
				}

				int x = Math.abs(check.x1 - check.x2);
				int z = Math.abs(check.z1 - check.z2);

				if (x < 5 || z < 5) {
					player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least (§f5 x 5§c)!");
					return;
				}

				if (x >= 3 * z || z >= 3 * x) {
					player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
					return;
				}

			}

			clearPillarAt(corner2);
			this.corner2 = to;

		}

		player.sendMessage(ChatColor.YELLOW + "Set claim's location §d" + loc + "§e to §b(§f" + to.getBlockX() + ", " + to.getBlockY() + ", " + to.getBlockZ() + "§b)§e.");

		Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> erectPillar(to, Material.EMERALD_BLOCK), 1L);

		int price = getPrice();

		if (price != -1) {
			Claim cc = new Claim(corner1, corner2);

			int x = Math.abs(cc.x1 - cc.x2);
			int z = Math.abs(cc.z1 - cc.z2);

			player.sendMessage("§eClaim cost: §f" + price + "§e; Current size: (§f" + x + "§e, §f" + z + "§e); §f" + (x * z) + "§e blocks");
		}
	}

	public void cancel(boolean complete) {
		if (complete && type != VisualType.MAP) {
			clearPillarAt(corner1);
			clearPillarAt(corner2);
		}

		if (type != VisualType.MAP) {
			player.getInventory().remove(net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Claim.SELECTION_WAND);
		}

		HandlerList.unregisterAll(this);
		if (type == VisualType.MAP) {
			currentMaps.remove(player.getName());

			if (mapBlocksSent.containsKey(player.getName())) {
				mapBlocksSent.get(player.getName()).forEach(l -> player.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData()));
			}
			mapBlocksSent.remove(player.getName());

		} else {
			visualClaims.remove(player.getName());
		}

		if (packetBlocksSent.containsKey(player.getName())) {
			packetBlocksSent.get(player.getName()).forEach(l -> player.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData()));
		}
		packetBlocksSent.remove(player.getName());

	}

	public void purchaseClaim() {
		if (!FoxtrotPlugin.getInstance().getTeamHandler().isOnTeam(player.getName())) {
			player.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
			cancel(true);
			return;

		}
		if (corner1 != null && corner2 != null) {
			int price = getPrice();

			Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

			if (!bypass && t.getClaims().size() >= t.getMaxClaimAmount()) {
				player.sendMessage(ChatColor.RED + "Your team has the maximum amount of claims, which is " + t.getMaxClaimAmount());
				return;
			}

			if (!bypass && !t.isCaptain(player.getName()) && !t.isOwner(player.getName())) {
				player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
				return;
			}

			if (!bypass && t.getBalance() < price) {
				player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
				return;
			}

            if (!bypass && t.isRaidable()) {
                player.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                return;
            }

			Claim cc = new Claim(corner1, corner2);

			if (!bypass && containsOtherClaim(cc)) {
				player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
				return;
			}

			HashSet<Claim> touching = touchesOtherClaim(cc);

			HashSet<Claim> cloneCheck = new HashSet<Claim>();
			touching.forEach(tee -> cloneCheck.add(tee.clone()));

			boolean contains = cloneCheck.removeIf(c -> t.ownsClaim(c));
			if (!bypass && t.getClaims().size() > 0 && !contains) {
				player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
				return;
			}

			if (!bypass && (touching.size() > 1 || (touching.size() == 1 && !contains))) {
				player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
				return;
			}

			int x = Math.abs(cc.x1 - cc.x2);
			int z = Math.abs(cc.z1 - cc.z2);

			if (!bypass && x < 5 || z < 5) {
				player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least (§f5 x 5§c)!");
				return;
			}

			if (!bypass && x >= 3 * z || z >= 3 * x) {
				player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
				return;
			}

			cc.setName(t.getName() + "_" + (100 + new Random().nextInt(800)));
			cc.setY1(0);
			cc.setY2(256);

			LandBoard.getInstance().setTeamAt(cc, t);
			t.getClaims().add(cc);

			player.sendMessage(ChatColor.LIGHT_PURPLE + "You have claimed this land for your team!");
			t.setBalance(t.getBalance() - price);
			player.sendMessage(ChatColor.YELLOW + "Your team's new balance is §f" + t.getBalance() + " §d(Price: " + price + ")");
            FactionActionTracker.logAction(t, "actions", "Land Claim: [" + cc.getMinimumPoint().getBlockX() + ", " + cc.getMinimumPoint().getBlockY() + ", " + cc.getMinimumPoint().getBlockZ() + "] -> [" + cc.getMaximumPoint().getBlockX() + ", " + cc.getMaximumPoint().getBlockY() + ", " + cc.getMaximumPoint().getBlockZ() + "] [Claimed by: " + player.getName() + ", Cost: " + price + "]");
			cancel(true);
		} else {
			player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
		}
	}

	public int getPrice() {
		if (corner1 != null && corner2 != null) {
			Claim cc = new Claim(corner1, corner2);

            return Claim.getPrice(cc, FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName()), true);
		}

		return -1;

	}

	private void drawPillars(Claim c, Material mat) {

		for (Location loc : c.getCornerLocations()) {
			erectPillar(loc, mat);
		}
	}

	private void erectPillar(Location loc, Material mat) {
		Location set = loc.clone();
	    List<Location> locs = new ArrayList<Location>();

		if (type == VisualType.MAP) {
			if (mapBlocksSent.containsKey(player.getName())) {
				locs = mapBlocksSent.get(player.getName());
			}
		} else {
			if (packetBlocksSent.containsKey(player.getName())) {
				locs = packetBlocksSent.get(player.getName());
			}
		}

		for (int i = 0; i < 256; i++) {
			set.setY(i);

			if (set.getBlock().getType() == Material.AIR || set.getBlock().getType().isTransparent()) {
				if (i % 5 == 0) {
					player.sendBlockChange(set, mat, (byte) 0);
				} else {
					player.sendBlockChange(set, Material.GLASS, (byte) 0);
				}

				locs.add(set.clone());
			}

		}
		if (type == VisualType.MAP) {
			mapBlocksSent.put(player.getName(), locs);
		} else {
			packetBlocksSent.put(player.getName(), locs);
		}
	}

	private void clearPillarAt(Location loc) {
		if (packetBlocksSent.containsKey(player.getName()) && loc != null) {
			packetBlocksSent.get(player.getName()).removeIf(l -> {
				if (l.getBlockX() == loc.getBlockX() && l.getBlockZ() == loc.getBlockZ()) {
					player.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData());
					return true;
				}
				return false;
			});
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getPlayer() == player && (type == VisualType.CREATE || type == VisualType.RESIZE)) {
			if (player.getItemInHand() != null && player.getItemInHand().getType() == Material.WOOD_HOE) {
				e.setCancelled(true);
				e.setUseInteractedBlock(Result.DENY);

				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

					if (!bypass && !FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(e.getClickedBlock().getLocation())) {
						player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
						return;
					}

					setLoc(2, e.getClickedBlock().getLocation());
				} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {

					if (!bypass && !FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(e.getClickedBlock().getLocation())) {
						player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
						return;
					}

					if (player.isSneaking()) {
						purchaseClaim();
					} else {
						setLoc(1, e.getClickedBlock().getLocation());
					}

				} else if (e.getAction() == Action.LEFT_CLICK_AIR && player.isSneaking()) {
					purchaseClaim();
				} else if (e.getAction() == Action.RIGHT_CLICK_AIR) {
					cancel(false);
					player.sendMessage(ChatColor.RED + "You have unset your first and second locations!");
				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (player == e.getPlayer()) {
			cancel(true);
		}
	}

	public Material getMaterial(int iteration) {
        if (iteration == -1) {
            return (Material.IRON_BLOCK);
        }

		while (iteration >= MAP_MATERIALS.length) {
			iteration = iteration - MAP_MATERIALS.length;
		}

		return (MAP_MATERIALS[iteration]);
	}

	public static VisualClaim getVisualClaim(String name) {
		return (visualClaims.get(name));
	}

	public static enum VisualType {
		MAP,
		RESIZE,
		CREATE
	}

}