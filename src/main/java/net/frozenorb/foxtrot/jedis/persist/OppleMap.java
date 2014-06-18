package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class OppleMap extends RedisPersistMap<Long> {

	public OppleMap() {
		super("opple_cooldown");
	}

	@Override
	public String getRedisValue(Long t) {
		return t + "";
	}

	@Override
	public Long getJavaObject(String str) {
		return Long.parseLong(str);
	}

}
