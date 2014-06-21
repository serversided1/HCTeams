package net.frozenorb.foxtrot.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.ClaimedChunk;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamManager;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

@SuppressWarnings("deprecation")
public class ServerManager {
	public static final int WARZONE_RADIUS = 512;

	public static final int[] DISALLOWED_POTIONS = { 8225, 16417, 16449, 16386,
			16418, 16450, 16387, 8228, 8260, 16420, 16452, 8200, 8264, 16392,
			16456, 8201, 8233, 8265, 16393, 16425, 16457, 8234, 16426, 16458,
			8204, 8236, 8268, 16396, 16428, 16460, 16398, 16462, 8257, 8193 };

	@Getter private static HashMap<String, Integer> tasks = new HashMap<String, Integer>();
	@Getter private static HashMap<Enchantment, Integer> maxEnchantments = new HashMap<Enchantment, Integer>();

	@Getter private HashSet<String> usedNames = new HashSet<String>();

	public ServerManager() {
		try {
			File f = new File("usedNames.json");
			if (!f.exists()) {
				f.createNewFile();
			}

			BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(f));

			if (dbo != null) {
				for (Object o : (BasicDBList) dbo.get("names")) {
					usedNames.add((String) o);
				}
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		loadEnchantments();
	}

	public void save() {

		try {
			File f = new File("usedNames.json");
			if (!f.exists()) {
				f.createNewFile();
			}

			BasicDBObject dbo = new BasicDBObject();
			BasicDBList list = new BasicDBList();

			for (String n : usedNames) {
				list.add(n);
			}

			dbo.put("names", list);
			FileUtils.write(f, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));

		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public boolean isBannedPotion(int value) {
		for (int i : DISALLOWED_POTIONS) {
			if (i == value) {
				return true;
			}
		}
		return false;
	}

	public boolean isWarzone(Location loc) {

		if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
			return false;
		}

		int x = loc.getBlockX();

		if (x > WARZONE_RADIUS) {
			return false;
		}

		int z = getWarzoneZ(x);

		if (loc.getBlockZ() > z || loc.getBlockZ() < -z) {
			return false;
		}
		return true;

	}

	public int getWarzoneZ(int x) {
		int radius = WARZONE_RADIUS;

		int xp = x - 1;
		int zp = (int) (Math.sqrt(radius * radius - xp * xp) + 0.5);
		int z = (int) (Math.sqrt(radius * radius - (x - 1) * x) + 0.5);

		if (zp > z)
			z = zp;

		return z;
	}

	public Location getSpawn(World w) {
		w = Bukkit.getWorld("world");
		Location l = w.getSpawnLocation().add(new Vector(0.5, 1, 0.5));
		l.setWorld(Bukkit.getServer().getWorlds().get(0));
		return l;
	}

	public boolean canWarp(Player player) {
		int max = 26;
		List<Entity> nearbyEntities = player.getNearbyEntities(max, max, max);

		if (player.getGameMode() == GameMode.CREATIVE) {
			return true;
		}
		Team warpeeTeam = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(player.getName());

		for (Entity e : nearbyEntities) {
			if ((e instanceof Player)) {
				Player p = (Player) e;
				if (!p.canSee(player)) {
					return true;
				}
				if (!player.canSee(p)) {
					continue;
				}

				Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

				if (team == null || warpeeTeam == null) {
					return false;
				}
				if (team != warpeeTeam)
					return false;

				if (team == warpeeTeam)
					continue;

			}
		}

		return true;
	}

	/**
	 * Gets whether two names are on the same team
	 * 
	 * @param s1
	 *            player1's name
	 * @param s2
	 *            player2's name
	 * @return same team
	 */
	public boolean areOnSameTeam(String s1, String s2) {
		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(s1);
		Team warpeeTeam = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(s2);

		if (team == null || warpeeTeam == null) {
			return false;
		}
		if (team != warpeeTeam)
			return false;

		if (team == warpeeTeam)
			return true;
		return false;

	}

	public void startLogoutSequence(final Player player) {
		player.sendMessage(ChatColor.YELLOW + "§lLogging out... §ePlease wait§c 30§e seconds.");
		final AtomicInteger seconds = new AtomicInteger(30);

		BukkitTask taskid = new BukkitRunnable() {

			@Override
			public void run() {

				seconds.set(seconds.get() - 1);
				player.sendMessage(ChatColor.RED + "" + seconds.get() + "§e seconds...");

				if (seconds.get() == 0) {
					if (tasks.containsKey(player.getName())) {
						tasks.remove(player.getName());
						player.setMetadata("loggedout", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
						player.kickPlayer("§cYou have been safely logged out of the server!");
						cancel();

					}
				}

			}
		}.runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);

		if (tasks.containsKey(player.getName())) {
			Bukkit.getScheduler().cancelTask(tasks.remove(player.getName()));
		}
		tasks.put(player.getName(), taskid.getTaskId());

	}

	public void beginWarp(final Player player, final Location to, int sec) {

		if (player.getGameMode() == GameMode.CREATIVE || player.hasMetadata("invisible")) {

			player.teleport(to);
			return;
		}

		if (isWarzone(player.getLocation())) {
			player.sendMessage(ChatColor.RED + "You cannot warp in the Warzone!");
			return;
		}
		TeamManager tm = FoxtrotPlugin.getInstance().getTeamManager();
		boolean enemyWithinRange = false;

		for (Entity e : player.getNearbyEntities(40, 256, 40)) {
			if (e instanceof Player) {
				Player other = (Player) e;

				if (other.hasMetadata("invisible")) {
					continue;
				}

				if (tm.getPlayerTeam(other.getName()) != tm.getPlayerTeam(player.getName())) {
					enemyWithinRange = true;
				}

			}
		}

		if (enemyWithinRange) {
			player.sendMessage(ChatColor.RED + "You cannot warp because an enemy is nearby!");
			return;

		}
		if (((Damageable) player).getHealth() != ((Damageable) player).getMaxHealth()) {
			player.sendMessage(ChatColor.RED + "You cannot warp because you do not have full health!");
			return;

		}

		if (player.getFoodLevel() != 20) {
			player.sendMessage(ChatColor.RED + "You cannot warp because you do not have full hunger!");

			return;
		}

		player.teleport(to);
		return;

	}

	public boolean isUnclaimed(Location loc) {
		return !FoxtrotPlugin.getInstance().getTeamManager().isTaken(new ClaimedChunk(loc.getChunk().getX(), loc.getChunk().getZ())) && !isWarzone(loc);
	}

	public boolean isAdminOverride(Player p) {
		return p.getGameMode() == GameMode.CREATIVE;
	}

	public Location getRandomSpawnLocation() {
		double angle = Math.random() * Math.PI * 2;

		int x = (int) (Math.cos(angle) * ServerManager.WARZONE_RADIUS * 2);
		int z = (int) (Math.sin(angle) * ServerManager.WARZONE_RADIUS * 2);

		World w = Bukkit.getWorlds().get(0);
		return new Location(w, x, w.getHighestBlockYAt(x, z), z);
	}

	public boolean isClaimedAndRaidable(Location loc) {

		Chunk c = loc.getChunk();
		Team owner = FoxtrotPlugin.getInstance().getTeamManager().getOwner(new ClaimedChunk(c.getX(), c.getZ()));

		return owner != null && owner.isRaidaible();

	}

	/**
	 * Disables a player from attacking for 10 seconds
	 * 
	 * @param p
	 *            the player to disable
	 */
	public void disablePlayerAttacking(final Player p, int seconds) {
		if (seconds == 10) {
			p.sendMessage(ChatColor.GRAY + "You cannot attack for " + seconds + " seconds.");
		}

		final Listener l = new Listener() {
			@EventHandler
			public void onPlayerDamage(EntityDamageByEntityEvent e) {
				if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
					if (((Player) e.getDamager()).getName().equals(p.getName())) {
						e.setCancelled(true);
					}
				}

			}

			@EventHandler(priority = EventPriority.HIGHEST,
					ignoreCancelled = true)
			public void onProjectileLaunch(ProjectileLaunchEvent e) {

				if (e.getEntityType() == EntityType.ENDER_PEARL) {
					Player p = (Player) e.getEntity().getShooter();
					e.setCancelled(true);
					p.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
					p.updateInventory();
				}

			}
		};
		Bukkit.getPluginManager().registerEvents(l, FoxtrotPlugin.getInstance());
		Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
			public void run() {
				HandlerList.unregisterAll(l);
			}
		}, seconds * 20);
	}

	public boolean isKOTHArena(Location loc) {
		for (CuboidRegion cr : RegionManager.get().getApplicableRegions(loc)) {
			if (cr.getName().startsWith("koth_")) {
				return true;
			}
		}
		return false;
	}

	public boolean isDiamondMountain(Location loc) {

		for (CuboidRegion cr : RegionManager.get().getApplicableRegions(loc)) {
			if (cr.getName().toLowerCase().startsWith("diamond")) {
				return true;
			}
		}
		return false;

	}

	public ArrayList<Team> getOnlineTeams() {
		ArrayList<Team> teams = new ArrayList<Team>();

		for (Player p : Bukkit.getOnlinePlayers()) {
			Team t = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

			if (t != null) {
				teams.add(t);
			}
		}
		return teams;
	}

	public void loadEnchantments() {
		maxEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		maxEnchantments.put(Enchantment.THORNS, -1);
		maxEnchantments.put(Enchantment.PROTECTION_PROJECTILE, 4);
		maxEnchantments.put(Enchantment.PROTECTION_EXPLOSIONS, 4);
		maxEnchantments.put(Enchantment.PROTECTION_FIRE, 4);
		maxEnchantments.put(Enchantment.PROTECTION_FALL, 4);
		maxEnchantments.put(Enchantment.DURABILITY, 3);
		maxEnchantments.put(Enchantment.OXYGEN, 3);
		maxEnchantments.put(Enchantment.WATER_WORKER, 1);

		maxEnchantments.put(Enchantment.DAMAGE_ALL, 2);
		maxEnchantments.put(Enchantment.FIRE_ASPECT, 2);
		maxEnchantments.put(Enchantment.KNOCKBACK, 1);
		maxEnchantments.put(Enchantment.DAMAGE_ARTHROPODS, 5);
		maxEnchantments.put(Enchantment.DAMAGE_UNDEAD, 5);
		maxEnchantments.put(Enchantment.ARROW_DAMAGE, 5);
		maxEnchantments.put(Enchantment.ARROW_FIRE, 1);
		maxEnchantments.put(Enchantment.ARROW_INFINITE, 1);
		maxEnchantments.put(Enchantment.ARROW_KNOCKBACK, 2);

		maxEnchantments.put(Enchantment.DIG_SPEED, 5);
		maxEnchantments.put(Enchantment.LOOT_BONUS_BLOCKS, 3);
		maxEnchantments.put(Enchantment.LOOT_BONUS_BLOCKS, 3);
		maxEnchantments.put(Enchantment.SILK_TOUCH, 1);
		maxEnchantments.put(Enchantment.LUCK, 3);
		maxEnchantments.put(Enchantment.LURE, 3);

	}
}
