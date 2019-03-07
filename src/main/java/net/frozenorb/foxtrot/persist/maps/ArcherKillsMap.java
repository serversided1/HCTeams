package net.frozenorb.foxtrot.persist.maps;

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

}
