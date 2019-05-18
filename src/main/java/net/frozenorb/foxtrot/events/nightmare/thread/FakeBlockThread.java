package net.frozenorb.foxtrot.events.nightmare.thread;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.events.nightmare.NightmareHandler;
import net.frozenorb.foxtrot.events.nightmare.progress.ProgressData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FakeBlockThread extends Thread {

	private final NightmareHandler handler;
	private final Map<String, Map<Location, Long>> sentBlockChanges = new HashMap<>();

	public FakeBlockThread(NightmareHandler handler) {
		super("Foxtrot - Fake Block Thread");

		this.handler = handler;
	}

	@Override
	public void run() {
		//noinspection InfiniteLoopStatement
		while (true) {
			for (Player player : handler.getWorld().getPlayers()) {
				try {
					checkPlayer(player);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(250L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkPlayer(Player player) {
		try {
			if (!handler.hasProgression(player)) {
				return;
			}

			ProgressData progressData = handler.getOrCreateProgression(player.getUniqueId());

			List<Location> blockUpdateLocations = new LinkedList<>(progressData.getAirBlocks());

			if (blockUpdateLocations.size() == 0) {
				clearPlayer(player);
			} else {
				if (!sentBlockChanges.containsKey(player.getName())) {
					sentBlockChanges.put(player.getName(), new HashMap<>());
				}

				Iterator<Map.Entry<Location, Long>> bordersIterator = sentBlockChanges.get(player.getName()).entrySet().iterator();

				while (bordersIterator.hasNext()) {
					Map.Entry<Location, Long> border = bordersIterator.next();

					if (System.currentTimeMillis() >= border.getValue()) {
						Location loc = border.getKey();

						if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) {
							continue;
						}

						Block block = loc.getBlock();
						player.sendBlockChange(loc, block.getType(), block.getData());
						bordersIterator.remove();
					}
				}

				for (Location location : blockUpdateLocations) {
					sendUpdateToPlayer(player, location);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendUpdateToPlayer(Player player, Location location) {
		if (location.distanceSquared(player.getLocation()) > 64) {
			return;
		}

		if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4) && location.distanceSquared(location) < 64) {
			player.sendBlockChange(location, Material.AIR, (byte) 0);
			sentBlockChanges.get(player.getName()).put(location, System.currentTimeMillis() + 4000L);
		}
	}

	private void clearPlayer(Player player) {
		if (sentBlockChanges.containsKey(player.getName())) {
			for (Location changedLoc : sentBlockChanges.get(player.getName()).keySet()) {
				if (!changedLoc.getWorld().isChunkLoaded(changedLoc.getBlockX() >> 4, changedLoc.getBlockZ() >> 4)) {
					continue;
				}

				Block block = changedLoc.getBlock();
				player.sendBlockChange(changedLoc, block.getType(), block.getData());
			}

			sentBlockChanges.remove(player.getName());
		}
	}

}
