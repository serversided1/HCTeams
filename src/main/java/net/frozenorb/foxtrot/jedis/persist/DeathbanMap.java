package net.frozenorb.foxtrot.jedis.persist;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class DeathbanMap extends RedisPersistMap<Long> {

	public DeathbanMap() {
		super("deathban");
	}

	@Override
	public String getRedisValue(Long t) {
		return t + "";
	}

	@Override
	public Long getJavaObject(String str) {
		return Long.parseLong(str);
	}

	public boolean isDeathbanned(Player p) {
		if (getValue(p.getName()) != null) {
			return getValue(p.getName()) > System.currentTimeMillis();
		}
		return false;
	}

	public boolean isDeathbanned(String name) {
		if (getValue(name) != null) {
			return getValue(name) > System.currentTimeMillis();
		}
		return false;
	}

	public void deathban(Player player, long seconds) {
		deathban(player.getName(), seconds);
	}

	public void deathban(String player, long seconds) {
		updateValue(player.toLowerCase(), System.currentTimeMillis() + seconds * 1000);
	}

}
