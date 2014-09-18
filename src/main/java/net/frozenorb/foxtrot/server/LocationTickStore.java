package net.frozenorb.foxtrot.server;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class LocationTickStore extends BukkitRunnable {
	private static LocationTickStore instance;
	private HashMap<String, LinkedList<Location>> storeLocations = new HashMap<String, LinkedList<Location>>();

	public static LocationTickStore getInstance() {
		if (instance == null) {
			instance = new LocationTickStore();
		}
		return instance;
	}

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			LinkedList<Location> locs = new LinkedList<>();

			if (storeLocations.containsKey(p.getName())) {
				locs = storeLocations.get(p.getName());
			}

			if (locs.size() > 0 && locs.getLast() != null && locs.getLast().equals(p.getLocation())) {
				continue;
			}

			locs.addLast(p.getLocation());

			if (locs.size() > 2) {
				locs.removeFirst();
			}

			storeLocations.put(p.getName(), locs);

		}
	}

	public Location recallOldestLocation(String name) {
		if (storeLocations.containsKey(name)) {
			return storeLocations.get(name).getFirst();
		}
		return null;
	}
}
