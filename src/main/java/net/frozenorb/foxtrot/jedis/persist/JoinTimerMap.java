package net.frozenorb.foxtrot.jedis.persist;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class JoinTimerMap extends RedisPersistMap<Long> {

	public JoinTimerMap() {
		super("join_timer");
	}

	@Override
	public Long getJavaObject(String str) {
		return Long.parseLong(str);
	}

	@Override
	public String getRedisValue(Long t) {
		return t + "";
	}

	public void createTimer(Player p, int seconds) {
		updateValue(p.getName(), System.currentTimeMillis() + (seconds * 1000));
	}

	public boolean hasTimer(Player p) {
		if (getValue(p.getName()) != null) {
			return getValue(p.getName()) > System.currentTimeMillis();
		}
		return false;
	}

}
