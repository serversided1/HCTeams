package net.frozenorb.foxtrot.game.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import lombok.AllArgsConstructor;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.Utilities.Types.Scrollable;
import net.frozenorb.foxtrot.game.Minigame;
import net.frozenorb.foxtrot.serialization.serializers.LocationSerializer;
import net.frozenorb.foxtrot.team.Team;

/**
 * An online game classic, the person to capture the hill wins the event.
 * 
 * @author Kerem Celik
 * 
 */
@SuppressWarnings("deprecation")
public class KingOfTheHill extends Minigame {

	private transient ArrayList<Location> spawnLocations = new ArrayList<Location>();

	private transient CuboidRegion hillRegion;
	private transient HashMap<Team, BukkitRunnable> taskMap = new HashMap<Team, BukkitRunnable>();
	private transient HashMap<Team, Integer> progress = new HashMap<Team, Integer>();
	private Listener listener = new PregameListener();

	public KingOfTheHill() {
		hillRegion = RegionManager.get().getByName("hillRegion");
	}

	@Override
	public String getName() {
		return "King of the Hill";
	}

	@Override
	public String getPrimaryCommandName() {
		return "koth";
	}

	@Override
	public void beginGame() {
		HandlerList.unregisterAll(listener);

		for (final Player p : getPlayers()) {

			progress.put(p.getName(), 0);
			PracticePlugin.get().getBossBarManager().registerMessage(p, new KOTHScroller(p));

		}
	}

	@Override
	public void onAnnounce() {
		Bukkit.getPluginManager().registerEvents(listener, PracticePlugin.get());
	}

	@Override
	public void onJoin(Player p) {
		p.teleport(spawnLocations.get(new Random().nextInt(spawnLocations.size())));
	}

	@Override
	public void cleanup() {
		HandlerList.unregisterAll(listener);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerLaunchProjectile(ProjectileLaunchEvent e) {
		if (e.getEntity() instanceof EnderPearl) {
			if (e.getEntity().getShooter() instanceof Player && getPlayers().contains((Player) e.getEntity().getShooter())) {
				e.getEntity().remove();
				e.setCancelled(true);
				((Player) e.getEntity().getShooter()).sendMessage(ChatColor.RED + "Enderpearls are disabled in this event!");

			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (hillRegion.contains(e.getTo())) {
			if (!taskMap.containsKey(e.getPlayer().getName())) {
				taskMap.put(e.getPlayer().getName(), new TaskRunnable(e.getPlayer()));

				taskMap.get(e.getPlayer().getName()).runTaskTimer(PracticePlugin.get(), 20L, 20L);
			}
		}
		if (hillRegion.contains(e.getFrom()) && !hillRegion.contains(e.getTo())) {
			if (taskMap.containsKey(e.getPlayer().getName())) {
				taskMap.get(e.getPlayer().getName()).cancel();
				taskMap.remove(e.getPlayer().getName());
			}
		}
	}

	@EventHandler
	public void onPlayerRespawn(final PlayerRespawnEvent e) {
		if (getPlayers().contains(e.getPlayer())) {
			e.getPlayer().sendMessage(MESSAGE_HEADER + "§eYou will respawn in 5 seconds.");
			Bukkit.getScheduler().runTaskLater(PracticePlugin.get(), new Runnable() {

				@Override
				public void run() {
					e.getPlayer().teleport(spawnLocations.get(new Random().nextInt(spawnLocations.size())));
				}
			}, 100L);
		}
	}

	/**
	 * Gets if the player is currently in the hill region.
	 * 
	 * @param p
	 *            the player to check
	 * @return player on hill
	 */
	private boolean isOnHill(Player p) {
		return (hillRegion.contains(p)) && !p.isDead();
	}

	@AllArgsConstructor
	private class KOTHScroller implements Scrollable {
		private Player p;

		@Override
		public String next() {
			boolean onHill = isOnHill(p);

			int percent = progress.get(p.getName()).intValue();

			return "§eYou are at §d" + percent + "%§e  -  " + (!onHill ? "§cNot capturing" : "§aCapturing");
		}
	}

	@AllArgsConstructor
	private class TaskRunnable extends BukkitRunnable {
		private Player p;

		@Override
		public void run() {

			if (!isOnHill(p) || p.isDead() || !p.isOnline() || !getPlayers().contains(p)) {
				cancel();
				taskMap.remove(p.getName());
				return;
			}
			int current = progress.get(p.getName());

			progress.put(p.getName(), current + 1);
			current = progress.get(p.getName());

			if ((current % 10 == 0 && current != 0) || current >= 95) {
				Bukkit.broadcastMessage(MESSAGE_HEADER + p.getDisplayName() + "§6 is at §c" + (current >= 95 ? "§l" : "") + (int) current + "§6% captured!");

			}

			if ((int) current >= 100) {
				finish(p);
				cancel();

				for (Player p : getPlayers()) {
					if (Bukkit.getScheduler().isCurrentlyRunning(taskMap.get(p.getName()).getTaskId())) {
						taskMap.get(p.getName()).cancel();
					}
				}
			}
		}
	}

	@Override
	public void deserialize(BasicDBObject dbobj) {
		BasicDBList spawns = (BasicDBList) dbobj.get("spawns");

		for (Object o : spawns) {
			spawnLocations.add(new LocationSerializer().deserialize((BasicDBObject) o));
		}
		super.deserialize(dbobj);

	}

	private class PregameListener implements Listener {
		@EventHandler
		public void onEntityDamage(EntityDamageEvent e) {
			if (e.getEntity() instanceof Player && getPlayers().contains((Player) e.getEntity()) && !isInProgress()) {
				e.setCancelled(true);
			}
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onPlayerLaunchProjectile(ProjectileLaunchEvent e) {
			if (e.getEntity() instanceof EnderPearl) {
				if (e.getEntity().getShooter() instanceof Player && getPlayers().contains((Player) e.getEntity().getShooter())) {
					e.getEntity().remove();
					e.setCancelled(true);
					((Player) e.getEntity().getShooter()).sendMessage(ChatColor.RED + "Enderpearls are disabled in this event!");

				}
			}
		}

	}

}
