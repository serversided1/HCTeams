package net.frozenorb.foxtrot.server;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.ClaimedChunk;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.org.apache.commons.io.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;

public class ServerManager {
	public static HashMap<String, Integer> tasks = new HashMap<String, Integer>();

	public static final int WARZONE_RADIUS = 512;
	@Getter private HashSet<String> usedNames = new HashSet<String>();
	@Getter private HashMap<Enchantment, Integer> maxEnchantments = new HashMap<Enchantment, Integer>();

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

	public boolean isWarzone(Location loc) {

		if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
			return false;
		}
		int radius = WARZONE_RADIUS;

		int x = loc.getBlockX();
		int xp = x - 1;
		int zp = (int) (Math.sqrt(radius * radius - xp * xp) + 0.5);
		int z = (int) (Math.sqrt(radius * radius - (x - 1) * x) + 0.5);

		if (zp > z)
			z = zp;
		if (loc.getBlockZ() > z || loc.getBlockZ() < -z) {
			return false;
		}
		return true;

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

	public void beginWarp(final Player player, final Location to, int sec) {

		final AtomicInteger seconds = new AtomicInteger(sec);
		if (player.getGameMode() == GameMode.CREATIVE || player.hasMetadata("invisible")) {

			player.teleport(to);
			return;
		}

		if (isWarzone(player.getLocation())) {
			player.sendMessage(ChatColor.RED + "You cannot warp in the Warzone!");
			return;
		}

		player.sendMessage(ChatColor.GRAY + "Warping in " + seconds.get() + " seconds. Don't move.");

		BukkitTask taskid = new BukkitRunnable() {

			@Override
			public void run() {

				seconds.set(seconds.get() - 1);

				if (seconds.get() % 5 == 0 || seconds.get() < 5) {
					player.sendMessage(ChatColor.GRAY + "Warping in " + seconds.get() + " seconds.");
				}
				if (seconds.get() == 0) {
					if (tasks.containsKey(player.getName())) {
						tasks.remove(player.getName());
						player.teleport(to);
						disablePlayerAttacking(player, 10);
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

	public boolean isUnclaimed(Location loc) {
		return !FoxtrotPlugin.getInstance().getTeamManager().isTaken(new ClaimedChunk(loc.getChunk().getX(), loc.getChunk().getZ())) && !isWarzone(loc);
	}

	public boolean isAdminOverride(Player p) {
		return p.getGameMode() == GameMode.CREATIVE || p.hasMetadata("invisible");
	}

	public Location getRandomSpawnLocation() {
		double angle = Math.random() * Math.PI * 2;

		int x = (int) (Math.cos(angle) * ServerManager.WARZONE_RADIUS * 2);
		int z = (int) (Math.sin(angle) * ServerManager.WARZONE_RADIUS * 2);

		World w = Bukkit.getWorlds().get(0);
		return new Location(w, x, w.getHighestBlockYAt(x, z), z);
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
		};
		Bukkit.getPluginManager().registerEvents(l, FoxtrotPlugin.getInstance());
		Bukkit.getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {
			public void run() {
				HandlerList.unregisterAll(l);
			}
		}, seconds * 20);
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

		maxEnchantments.put(Enchantment.DAMAGE_ALL, 3);
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
