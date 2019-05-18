package net.frozenorb.foxtrot.events.nightmare.mob;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.events.nightmare.NightmareHandler;
import net.frozenorb.foxtrot.events.nightmare.progress.ProgressData;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class MobLogicTask extends BukkitRunnable {

	private final NightmareHandler handler;

	@Override
	public void run() {
		// spawn mobs for teams that need more mobs spawned


		// set AI for mobs that need updates
		for (Entity entity : handler.getWorld().getEntities()) {
			if (entity instanceof EntityInsentient) {
				EntityInsentient insentient = (EntityInsentient) entity;
				EntityLiving target = insentient.getGoalTarget();

				if (target == null) {
					// Assign a new target
					if (insentient.getBukkitEntity().hasMetadata("ProgressData")) {
						ProgressData progressData = (ProgressData) insentient.getBukkitEntity().getMetadata("ProgressData").get(0);

						List<Player> inRadius = progressData.getParticipants()
						                                    .stream()
						                                    .map(Bukkit::getPlayer)
						                                    .filter(Objects::nonNull)
						                                    .filter(player -> player.getLocation().distance(entity.getLocation()) <= 10)
						                                    .collect(Collectors.toList());

						if (inRadius.isEmpty()) {
							continue;
						}

						Collections.shuffle(inRadius);

						insentient.setGoalTarget(((CraftPlayer) inRadius.get(0)).getHandle());
					} else {
						entity.remove();
					}
				} else {
					// Just set target to null and let this task assign a new target next run
					if (target instanceof Player) {
						Player targetPlayer = (Player) target;

						if (handler.hasProgression(targetPlayer)) {
							ProgressData progressData = handler.getOrCreateProgression(targetPlayer.getUniqueId());

							if (!progressData.getTrackedEntities().contains(entity)) {
								insentient.setGoalTarget(null);
							}
						} else {
							insentient.setGoalTarget(null);
						}
					} else {
						insentient.setGoalTarget(null);
					}
				}
			}
		}
	}

}
