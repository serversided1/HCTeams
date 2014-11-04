package net.frozenorb.foxtrot.team.claims;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim.CuboidDirection;
import net.frozenorb.foxtrot.team.claims.Claim.SpecialTag;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

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

	@Getter private static HashMap<String, VisualClaim> currentMaps = new HashMap<String, VisualClaim>();
	@Getter private static HashMap<String, VisualClaim> visualClaims = new HashMap<String, VisualClaim>();
	private static HashMap<String, ArrayList<Location>> packetBlocksSent = new HashMap<String, ArrayList<Location>>();
	private static HashMap<String, ArrayList<Location>> mapBlocksSent = new HashMap<String, ArrayList<Location>>();

	@Getter @NonNull private Player p;
	@NonNull private VisualType type;
    @NonNull private boolean bypass;

	@Setter private Claim outline;

	private Location corner1;
	private Location corner2;

	public void draw(boolean silent) {
		if (currentMaps.containsKey(p.getName()) && type == VisualType.MAP) {
			currentMaps.get(p.getName()).cancel(true);

			if (!silent) {
				p.sendMessage(ChatColor.YELLOW + "Claim pillars have been hidden!");
			}

			return;

		}

		if (visualClaims.containsKey(p.getName()) && type != VisualType.MAP) {
			visualClaims.get(p.getName()).cancel(true);
		}
		if (type == VisualType.CREATE || type == VisualType.RESIZE) {
			visualClaims.put(p.getName(), this);
		} else {
			currentMaps.put(p.getName(), this);
		}

		Bukkit.getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());

		int x = p.getLocation().getBlockX(), z = p.getLocation().getBlockZ();

		if (type == VisualType.MAP) {
			int iter = 0;

			HashMap<Team, Material> storageReference = new HashMap<Team, Material>();
			HashMap<Claim, Material> sendMaps = new HashMap<Claim, Material>();
			for (Claim c : LandBoard.getInstance().getClaims()) {
				if (c.isWithin(x, z, MAP_RADIUS)) {

					Team owner = LandBoard.getInstance().getTeamAt(c);
					Material mat = getMaterial(c, iter);

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
			spawn.setTag(SpecialTag.SPAWN);

			if (spawn.isWithin(x, z, MAP_RADIUS)) {
				Material mat = getMaterial(spawn, -1);

				drawPillars(spawn, mat);
				sendMaps.put(spawn, mat);

			}

			if (sendMaps.isEmpty()) {
				p.sendMessage(ChatColor.YELLOW + "There are no claims within " + MAP_RADIUS + " blocks of you!");
				cancel(true);
			}

			if (!silent) {
				sendMaps.forEach((c, m) -> {
					if (c.getTag() == SpecialTag.SPAWN) {
						p.sendMessage("§eLand §9Spawn§a(§b" + ItemDb.getFriendlyName(new ItemStack(m)) + "§a) §eis claimed by §9Spawn");

					} else {
						p.sendMessage("§eLand §9" + c.getName() + "§a(§b" + ItemDb.getFriendlyName(new ItemStack(m)) + "§a) §eis claimed by §9" + LandBoard.getInstance().getTeamAt(c).getFriendlyName());
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
		return claimed;
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

		if (!FoxtrotPlugin.getInstance().getTeamHandler().isOnTeam(p.getName())) {
			p.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
			cancel(true);
			return;
		}

		Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

		if (loc == 1) {

			if (corner2 != null) {
				Claim check = new Claim(to, corner2);

				if (!bypass && containsOtherClaim(check)) {
					p.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
					return;
				}

				HashSet<Claim> touching = touchesOtherClaim(check);
				HashSet<Claim> cloneCheck = new HashSet<Claim>();
				touching.forEach(tee -> cloneCheck.add(tee.clone()));

				boolean contains = cloneCheck.removeIf(c -> t.ownsClaim(c));
				if (t.getClaims().size() > 0 && !contains) {
					p.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
					return;
				}

				if (touching.size() > 1 || (touching.size() == 1 && !contains)) {
					p.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
					return;
				}

				int x = Math.abs(check.x1 - check.x2);
				int z = Math.abs(check.z1 - check.z2);

				if (x < 4 || z < 4) {
					p.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least (§f5 x 5§c)!");
					return;
				}

				if (x >= 3 * z || z >= 3 * x) {
					p.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
					return;
				}

			}

			clearPillarAt(corner1);
			this.corner1 = to;

		} else if (loc == 2) {

			if (corner1 != null) {
				Claim check = new Claim(corner1, to);

				if (!bypass && containsOtherClaim(check)) {
					p.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
					return;
				}

				HashSet<Claim> touching = touchesOtherClaim(check);

				HashSet<Claim> cloneCheck = new HashSet<Claim>();
				touching.forEach(tee -> cloneCheck.add(tee.clone()));

				boolean contains = cloneCheck.removeIf(c -> t.ownsClaim(c));
				if (t.getClaims().size() > 0 && !contains) {
					p.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
					return;
				}

				if (touching.size() > 1 || (touching.size() == 1 && !contains)) {
					p.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
					return;
				}

				int x = Math.abs(check.x1 - check.x2);
				int z = Math.abs(check.z1 - check.z2);

				if (x < 5 || z < 5) {
					p.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least (§f5 x 5§c)!");
					return;
				}

				if (x >= 3 * z || z >= 3 * x) {
					p.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
					return;
				}

			}

			clearPillarAt(corner2);
			this.corner2 = to;

		}

		p.sendMessage(ChatColor.YELLOW + "Set claim's location §d" + loc + "§e to §b(§f" + to.getBlockX() + ", " + to.getBlockY() + ", " + to.getBlockZ() + "§b)§e.");

		Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), () -> erectPillar(to, Material.EMERALD_BLOCK), 1L);

		int price = getPrice();

		if (price != -1) {
			Claim cc = new Claim(corner1, corner2);

			int x = Math.abs(cc.x1 - cc.x2);
			int z = Math.abs(cc.z1 - cc.z2);

			p.sendMessage("§eClaim cost: §f" + price + "§e; Current size: (§f" + x + "§e, §f" + z + "§e); §f" + (x * z) + "§e blocks");
		}
	}

	public void cancel(boolean complete) {

		if (complete && type != VisualType.MAP) {

			clearPillarAt(corner1);
			clearPillarAt(corner2);
		}

		if (type != VisualType.MAP) {
			p.getInventory().remove(net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Claim.SELECTION_WAND);
		}

		HandlerList.unregisterAll(this);
		if (type == VisualType.MAP) {
			currentMaps.remove(p.getName());

			if (mapBlocksSent.containsKey(p.getName())) {
				mapBlocksSent.get(p.getName()).forEach(l -> p.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData()));
			}
			mapBlocksSent.remove(p.getName());

		} else {
			visualClaims.remove(p.getName());
		}

		if (packetBlocksSent.containsKey(p.getName())) {
			packetBlocksSent.get(p.getName()).forEach(l -> p.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData()));
		}
		packetBlocksSent.remove(p.getName());

	}

	public void purchaseClaim() {
		if (!FoxtrotPlugin.getInstance().getTeamHandler().isOnTeam(p.getName())) {
			p.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
			cancel(true);
			return;

		}
		if (corner1 != null && corner2 != null) {
			int price = getPrice();

			Team t = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

			if (t.getClaims().size() >= t.getMaxClaimAmount()) {
				p.sendMessage(ChatColor.RED + "Your team has the maximum amount of claims, which is " + t.getMaxClaimAmount());
				return;
			}

			if (!t.isCaptain(p.getName()) && !t.isOwner(p.getName())) {
				p.sendMessage(ChatColor.RED + "Only team captains can claim land.");
				return;
			}

			if (t.getBalance() < price) {
				p.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
				return;
			}

            if (t.isRaidable()) {
                p.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                return;
            }

			Claim cc = new Claim(corner1, corner2);

			if (!bypass && containsOtherClaim(cc)) {
				p.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
				return;
			}

			HashSet<Claim> touching = touchesOtherClaim(cc);

			HashSet<Claim> cloneCheck = new HashSet<Claim>();
			touching.forEach(tee -> cloneCheck.add(tee.clone()));

			boolean contains = cloneCheck.removeIf(c -> t.ownsClaim(c));
			if (t.getClaims().size() > 0 && !contains) {
				p.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
				return;
			}

			if (touching.size() > 1 || (touching.size() == 1 && !contains)) {
				p.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
				return;
			}

			int x = Math.abs(cc.x1 - cc.x2);
			int z = Math.abs(cc.z1 - cc.z2);

			if (x < 5 || z < 5) {
				p.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least (§f5 x 5§c)!");
				return;
			}

			if (x >= 3 * z || z >= 3 * x) {
				p.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
				return;
			}

			cc.setName(t.getName() + "_" + (100 + new Random().nextInt(800)));
			cc.setY1(0);
			cc.setY2(256);

			LandBoard.getInstance().setTeamAt(cc, t);
			t.getClaims().add(cc);

			p.sendMessage(ChatColor.LIGHT_PURPLE + "You have claimed this land for your team!");
			t.setBalance(t.getBalance() - price);
			p.sendMessage(ChatColor.YELLOW + "Your team's new balance is §f" + t.getBalance() + " §d(Price: " + price + ")");
			cancel(true);

		} else {
			p.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
		}
	}

	public int getPrice() {
		if (corner1 != null && corner2 != null) {
			Claim cc = new Claim(corner1, corner2);

            return Claim.getPrice(cc, FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName()), true);
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

		ArrayList<Location> locs = new ArrayList<Location>();

		if (type == VisualType.MAP) {
			if (mapBlocksSent.containsKey(p.getName())) {
				locs = mapBlocksSent.get(p.getName());
			}
		} else {

			if (packetBlocksSent.containsKey(p.getName())) {
				locs = packetBlocksSent.get(p.getName());
			}
		}

		for (int i = 0; i < 256; i++) {
			set.setY(i);

			if (set.getBlock().getType() == Material.AIR || set.getBlock().getType().isTransparent()) {
				if (i % 5 == 0) {
					p.sendBlockChange(set, mat, (byte) 0);
				} else {
					p.sendBlockChange(set, Material.GLASS, (byte) 0);

				}

				locs.add(set.clone());
			}

		}
		if (type == VisualType.MAP) {
			mapBlocksSent.put(p.getName(), locs);

		} else {
			packetBlocksSent.put(p.getName(), locs);
		}
	}

	private void clearPillarAt(Location loc) {
		if (packetBlocksSent.containsKey(p.getName()) && loc != null) {
			packetBlocksSent.get(p.getName()).removeIf(l -> {
				if (l.getBlockX() == loc.getBlockX() && l.getBlockZ() == loc.getBlockZ()) {
					p.sendBlockChange(l, l.getBlock().getType(), l.getBlock().getData());
					return true;
				}
				return false;
			});
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getPlayer() == p && (type == VisualType.CREATE || type == VisualType.RESIZE)) {
			if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.WOOD_HOE) {
				e.setCancelled(true);
				e.setUseInteractedBlock(Result.DENY);

				if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

					if (!bypass && !FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(e.getClickedBlock().getLocation())) {
						p.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
						return;
					}

					setLoc(2, e.getClickedBlock().getLocation());
				} else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {

					if (!bypass && !FoxtrotPlugin.getInstance().getServerHandler().isUnclaimed(e.getClickedBlock().getLocation())) {
						p.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
						return;
					}

					if (p.isSneaking()) {
						purchaseClaim();
					} else {
						setLoc(1, e.getClickedBlock().getLocation());
					}

				} else if (e.getAction() == Action.LEFT_CLICK_AIR && p.isSneaking()) {
					purchaseClaim();
				} else if (e.getAction() == Action.RIGHT_CLICK_AIR) {
					cancel(false);
					p.sendMessage(ChatColor.RED + "You have unset your first and second locations!");

				}
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (p == e.getPlayer()) {
			cancel(true);
		}
	}

	public Material getMaterial(Claim claim, int iteration) {
		if (claim.getTag() == SpecialTag.SPAWN) {
			return Material.IRON_BLOCK;
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