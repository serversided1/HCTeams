package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class ArcherKillsMap extends PersistMap<Integer> {

	public ArcherKillsMap() {
		super("ArcherKills", "KitMap.ArcherKills");
	}

	@Override
	public String getRedisValue(Integer integer) {
		return integer.toString();
	}

	@Override
	public Integer getJavaObject(String str) {
		return Integer.valueOf(str);
	}

	@Override
	public Object getMongoValue(Integer integer) {
		return integer;
	}

	public int getArcherKills(UUID check) {
		return (contains(check) ? getValue(check) : 0);
	}

	public void setArcherKills(UUID update, int archerKills) {
		updateValueAsync(update, archerKills);
	}

	public void increment(UUID update) {
		setArcherKills(update, getArcherKills(update) + 1);
	}

}
