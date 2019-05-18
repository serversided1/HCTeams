package net.frozenorb.foxtrot.events.nightmare.mob;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.events.nightmare.NightmareHandler;
import net.frozenorb.foxtrot.events.nightmare.progress.ProgressData;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class MobReferenceTask extends BukkitRunnable {

	private final NightmareHandler handler;

	@Override
	public void run() {
		// Remove references to entities that are dead
		for (ProgressData progressData : handler.getPlayerProgress().values()) {
			if (!progressData.isCleanup()) {
				progressData.getTrackedEntities().removeIf(Entity::isDead);
				progressData.getDroppedItems().removeIf(Entity::isDead);
			}

			progressData.setCleanup(true);
		}

		// Reset cleanup
		for (ProgressData progressData : handler.getPlayerProgress().values()) {
			progressData.setCleanup(false);
		}
	}

}
