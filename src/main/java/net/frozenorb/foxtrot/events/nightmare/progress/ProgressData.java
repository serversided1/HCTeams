package net.frozenorb.foxtrot.events.nightmare.progress;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@Getter
public class ProgressData {

	private static final long TIME_LIMIT_MS = 1_000L * 60 * 10 + 1_000L;

	private long startTime;
	private List<UUID> participants;
	private List<Entity> droppedItems;
	private List<Entity> trackedEntities;
	private List<Location> airBlocks;
	@Setter private int stagesComplete;
	@Setter private boolean cleanup;

	public ProgressData(UUID initialUuid) {
		this.startTime = System.currentTimeMillis();

		this.participants = new ArrayList<>();
		this.participants.add(initialUuid);

		this.droppedItems = new ArrayList<>();
		this.trackedEntities = new ArrayList<>();
		this.airBlocks = new ArrayList<>();
		this.stagesComplete = 0;
	}

	public boolean isTimesUp() {
		return startTime + TIME_LIMIT_MS < System.currentTimeMillis();
	}

	public long getTimeRemaining() {
		return startTime + TIME_LIMIT_MS - System.currentTimeMillis();
	}

	public boolean isTrackedId(int entityId) {
		for (Entity entity : trackedEntities) {
			if (entity.getEntityId() == entityId) {
				return true;
			}
		}

		return false;
	}

}
