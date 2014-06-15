package net.frozenorb.foxtrot.util;

import org.bukkit.Location;

public class TimestampedLocation {
	public long timestamp;
	public Location loc;
	public String data;

	public TimestampedLocation(Location loc, long ts) {
		this.loc = loc;
		this.timestamp = ts;
	}

	public TimestampedLocation setData(String data) {
		this.data = data;
		return this;
	}
}
