package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class KillsMap extends RedisPersistMap<Integer> {

	public KillsMap() {
		super("player_kills");
	}

	@Override
	public String getRedisValue(Integer t) {
		return t + "";
	}

	@Override
	public Integer getJavaObject(String str) {
		return Integer.parseInt(str);
	}

	public int getKills(String name) {
		return getValue(name.toLowerCase()) != null ? getValue(name.toLowerCase()) : 0;
	}
}
