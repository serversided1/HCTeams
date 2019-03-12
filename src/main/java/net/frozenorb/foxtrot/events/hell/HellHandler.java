package net.frozenorb.foxtrot.events.hell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HellHandler implements Listener {

	public static final long TIME_LIMIT_MS = 1_000L * 60 * 10;

	@Getter
	private World world;
	@Getter
	private Map<UUID, Long> playerToTime;
	private Map<UUID, Location> previousLocation;

	public HellHandler() {
		WorldCreator worldCreator = new WorldCreator(Foxtrot.getInstance().getConfig().getString("hell.worldName", "Hell-Grid"))
				.environment(World.Environment.NETHER);

		this.world = Bukkit.createWorld(worldCreator);
		this.playerToTime = new HashMap<>();
		this.previousLocation = new HashMap<>();

		// Register this class as a listener
		Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());

		// Register commands
		FrozenCommandHandler.registerPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.events.hell.command");

		// Register task
		Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
			List<UUID> toRemove = new ArrayList<>();

			for (Map.Entry<UUID, Long> entry : playerToTime.entrySet()) {
				if (Bukkit.getPlayer(entry.getKey()) == null) {
					toRemove.add(entry.getKey());
				}

//				if (entry.getValue() + TIME_LIMIT_MS >= System.currentTimeMillis()) {
//					toRemove.add(entry.getKey());
//				}
			}

			for (UUID uuid : toRemove) {
				playerToTime.remove(uuid);

				Player player = Bukkit.getPlayer(uuid);

				if (player != null) {
					if (player.getWorld().equals(this.world)) {
						player.sendMessage(ChatColor.RED + "The dream starts to fade...");
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 0));

						Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
							player.teleport(previousLocation.remove(player.getUniqueId()));
							player.sendMessage(ChatColor.RED + "That was one weird dream...");
						}, 20L * 3);
					}
				}
			}
		}, 0L, 10L);
	}

	public ItemStack getPotionItemStack() {
		return ItemBuilder.of(Material.POTION)
		                  .name(ChatColor.GOLD + "Potion of Dreams")
		                  .data((short) 8262)
		                  .addToLore(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Directions:")
		                  .addToLore(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Make sure someone brings a pick")
		                  .addToLore(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Only drink this if it's nighttime in the world")
		                  .addToLore(ChatColor.DARK_PURPLE + ChatColor.ITALIC.toString() + "Otherwise you will not be able to sleep in a bed")
		                  .build();
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		if (event.getItem().isSimilar(Foxtrot.getInstance().getHellHandler().getPotionItemStack())) {
			Player player = event.getPlayer();

			// Don't allow the player to drink multiple of the potions
			if (playerToTime.containsKey(event.getPlayer().getUniqueId())) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You already drank one of these...");
				return;
			}

			// Don't allow the player to start the event if they are on PvP timer
			if (Foxtrot.getInstance().getStartingPvPTimerMap().get(player.getUniqueId()) || Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You can't enter the dream world whilst you have PvP protection.");
				return;
			}

			player.sendMessage(ChatColor.RED + "You drink the the Potion of Dreams.");
			player.sendMessage(ChatColor.RED + "You're starting to feel awfully tired...");
			player.sendMessage(ChatColor.RED + player.getName() + ": Ugh... Maybe I should lay down...");

			// Give the player effects
			// (remove blindness and confusion once the player goes into the world)
			player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60 * 10, 1));
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 60 * 15, 0));
			player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 60 * 15, 0));

			// Put player in map
			playerToTime.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
		if (playerToTime.containsKey(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage("debug: you slept in a bed after drinking the potion");

			// Put player's location in map
			previousLocation.put(event.getPlayer().getUniqueId(), event.getPlayer().getLocation());

			// Teleport player to their faction's Hell Event island
			Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
				event.getPlayer().teleport(this.world.getSpawnLocation());
				event.getPlayer().removePotionEffect(PotionEffectType.BLINDNESS);
				event.getPlayer().removePotionEffect(PotionEffectType.CONFUSION);
			}, 20L * 3);
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player killer = null;

			if (event.getDamager() instanceof Player) {
				killer = (Player) event.getDamager();
			} else if (event.getDamager() instanceof Projectile) {
				if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
					killer = (Player) ((Projectile) event.getDamager()).getShooter();
				}
			}

			if (killer != null) {
				Player victim = (Player) event.getEntity();

				if (playerToTime.containsKey(victim.getUniqueId()) || playerToTime.containsKey(killer.getUniqueId())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		// Player is not apart of the Hell event
		if (!playerToTime.containsKey(event.getEntity().getUniqueId())) {
			return;
		}

		// Player is not in the Hell Grid world
		if (!event.getEntity().getWorld().equals(this.world)) {
			return;
		}

		// Player is in mod-mode
		if (event.getEntity().hasMetadata("modmode")) {
			return;
		}

		event.getEntity().sendMessage("debug: died in hell event");
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		if (playerToTime.remove(event.getPlayer().getUniqueId()) != null) {
			// Teleport them back to their previous location
			if (event.getPlayer().getWorld().equals(this.world)) {
				event.getPlayer().teleport(previousLocation.remove(event.getPlayer().getUniqueId()));
			}
		}
	}

}
