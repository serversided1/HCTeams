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
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamLocationType;
import net.frozenorb.foxtrot.team.TeamManager;
import net.frozenorb.mBasic.Basic;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	public static final int WARZONE_RADIUS = 1000;

	public static final int[] DISALLOWED_POTIONS = { 8225, 16417, 16449, 16386,
			16418, 16450, 16387, 8228, 8260, 16420, 16452, 8200, 8264, 16392,
			16456, 8201, 8233, 8265, 16393, 16425, 16457, 8234, 16458, 8204,
			8236, 8268, 16396, 16428, 16460, 16398, 16462, 8257, 8193, 16385,
			16424, 16430 };

	@Getter private static HashMap<String, Integer> tasks = new HashMap<String, Integer>();
	@Getter private static HashMap<Enchantment, Integer> maxEnchantments = new HashMap<Enchantment, Integer>();
	@Getter private HashMap<String, Long> fHomeCooldown = new HashMap<String, Long>();

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
		int z = loc.getBlockZ();

		return ((x < WARZONE_RADIUS && x > -WARZONE_RADIUS) && (z < WARZONE_RADIUS && z > -WARZONE_RADIUS));

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

	public RegionData<?> getRegion(Location loc, Player p) {

		if (isSpawn(loc)) {
			return new RegionData<Object>(loc, Region.SPAWN, null);
		}

		if (isDiamondMountain(loc)) {
			return new RegionData<Object>(loc, Region.DIAMOND_MOUNTAIN, null);
		}

		if (isKOTHArena(loc)) {

			String n = "";
			for (CuboidRegion rg : RegionManager.get().getApplicableRegions(loc)) {
				if (rg.getName().startsWith("koth_")) {
					n = rg.getName().replace("koth_", "");
					break;
				}
			}

			return new RegionData<String>(loc, Region.KOTH_ARENA, n);
		}

		if (isUnclaimed(loc)) {
			return new RegionData<Object>(loc, Region.WILDNERNESS, null);
		}

		if (isWarzone(loc)) {
			return new RegionData<Object>(loc, Region.WARZONE, null);
		}

		Team ownerTo = FoxtrotPlugin.getInstance().getTeamManager().getOwner(loc);
		return new RegionData<Team>(loc, Region.CLAIMED_LAND, ownerTo);

	}

	public void beginWarp(final Player player, final Location to, int price, TeamLocationType type) {

		if (player.getGameMode() == GameMode.CREATIVE || player.hasMetadata("invisible")) {

			player.teleport(to);
			return;
		}

		TeamManager tm = FoxtrotPlugin.getInstance().getTeamManager();

		if (type == TeamLocationType.HOME) {
			double bal = tm.getPlayerTeam(player.getName()).getBalance();

			if (bal < price) {
				player.sendMessage(ChatColor.RED + "This costs §e$" + price + "§c while your team has only §e$" + bal + "§c!");
				return;
			}
		} else {

			double bal = Basic.get().getEconomyManager().getBalance(player.getName());

			if (bal < price) {
				player.sendMessage(ChatColor.RED + "This costs §e$" + price + "§c while you have §e$" + bal + "§c!");
				return;
			}

		}

		if (type == TeamLocationType.HOME && FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(player)) {
			player.sendMessage("You cannot warp home with a PVP Timer. Type '§e/pvptimer remove§c' to remove your timer.");
			return;
		}

		if (type == TeamLocationType.RALLY) {
			if (SpawnTag.isTagged(player)) {
				player.sendMessage(ChatColor.RED + "You cannot warp to rally while spawn-tagged!");
				return;
			}

		}
		if (FoxListener.getEnderpearlCooldown().containsKey(player.getName()) && FoxListener.getEnderpearlCooldown().get(player.getName()) > System.currentTimeMillis()) {
			player.sendMessage(ChatColor.RED + "You cannot warp while your enderpearl cooldown is active!");
			return;
		}

		boolean enemyWithinRange = false;

		for (Entity e : player.getNearbyEntities(30, 256, 30)) {
			if (e instanceof Player) {
				Player other = (Player) e;

				if (other.hasMetadata("invisible") || FoxtrotPlugin.getInstance().getJoinTimerMap().hasTimer(other)) {
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

		if (type == TeamLocationType.HOME) {
			player.sendMessage(ChatColor.YELLOW + "§d$" + price + " §ehas been deducted from your team balance.");
			tm.getPlayerTeam(player.getName()).setBalance(tm.getPlayerTeam(player.getName()).getBalance() - price);
		} else {
			Basic.get().getEconomyManager().withdrawPlayer(player.getName(), price);
			player.sendMessage(ChatColor.YELLOW + "§d$" + price + " §ehas been deducted from your balance.");

		}

		player.teleport(to);

		if (type == TeamLocationType.HOME) {
			FoxtrotPlugin.getInstance().getJoinTimerMap().updateValue(player.getName(), -1L);
		}

		if (type == TeamLocationType.RALLY) {
			fHomeCooldown.put(player.getName(), System.currentTimeMillis() + 15 * 60_000);
		}
		return;

	}

	public boolean isUnclaimed(Location loc) {
		return !FoxtrotPlugin.getInstance().getTeamManager().isTaken(loc) && !isWarzone(loc);
	}

	public boolean isAdminOverride(Player p) {
		return p.getGameMode() == GameMode.CREATIVE;
	}

	public Location getSpawnLocation() {
		World w = Bukkit.getWorld("world");

		Location l = w.getSpawnLocation().add(new Vector(0.5, 1, 0.5));
		l.setWorld(Bukkit.getServer().getWorlds().get(0));

		return l;
	}

	public boolean isSpawn(Location loc) {
		return RegionManager.get().hasTag(loc, "spawn");
	}

	public boolean isClaimedAndRaidable(Location loc) {

		Team owner = FoxtrotPlugin.getInstance().getTeamManager().getOwner(loc);

		return owner != null && owner.isRaidaible();

	}

	public int getLives(String name) {
		return 0;
	}

	public void setLives(String name, int lives) {

	}

	public void revivePlayer(String name) {
		FoxtrotPlugin.getInstance().getDeathbanMap().updateValue(name, 0L);

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
		maxEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
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
