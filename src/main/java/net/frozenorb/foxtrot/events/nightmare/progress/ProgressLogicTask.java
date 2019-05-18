package net.frozenorb.foxtrot.events.nightmare.progress;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.nightmare.NightmareHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class ProgressLogicTask extends BukkitRunnable {

	private final NightmareHandler handler;

	@Override
	public void run() {
		// Don't process loop if the event is disabled and
		// there aren't any players to iterate through
		if (handler.isAdminDisabled() && handler.getPlayerProgress().isEmpty()) {
			return;
		}

		List<UUID> toRemove = new ArrayList<>();

		// The player's progress is cleared if any of the
		// following criteria is met:
		//
		// 1. If the player is offline
		// 2. If the player's progression time has passed
		//    the limit
		for (Map.Entry<UUID, ProgressData> entry : handler.getPlayerProgress().entrySet()) {
			if (Bukkit.getPlayer(entry.getKey()) == null) {
				toRemove.add(entry.getKey());
			}

			if (entry.getValue().isTimesUp()) {
				toRemove.add(entry.getKey());
			}
		}

		// If the player is still online, add effects
		// and schedule a task to run later (3 seconds)
		// that teleports them to their previous location
		for (UUID uuid : toRemove) {
			handler.getPlayerProgress().remove(uuid);

			Player player = Bukkit.getPlayer(uuid);

			if (player != null) {
				if (player.getWorld().equals(handler.getWorld())) {
					((CraftPlayer) player).getHandle().allowServerSidePhase = false;
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0));
					player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 5, 0));
					player.sendMessage(ChatColor.RED + "The nightmare starts to fade...");

					Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
						player.teleport(handler.getPreviousLocations().remove(player.getUniqueId()));
						player.sendMessage(ChatColor.RED + "You ran out of time and woke up from the nightmare!");
					}, 20L * 3);
				}
			}
		}
	}

}
