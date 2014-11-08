package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.FoxtrotPlugin;
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

	public boolean isDeathbanned(Player player) {
		return (isDeathbanned(player.getName()));
	}

	public boolean isDeathbanned(String name) {
        if (getValue(name) != null) {
            if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                // Ignore deathbans less than 5 days (at EOTW we deathban for 10 days)
                return ((getValue(name) - System.currentTimeMillis()) > 1000L * 60 * 60 * 24 * 5);
            } else {
                return (getValue(name) > System.currentTimeMillis());
            }
        }

        return (false);
	}

	public void deathban(Player player, long seconds) {
		deathban(player.getName(), seconds);
	}

	public void deathban(String player, long seconds) {
		updateValue(player.toLowerCase(), System.currentTimeMillis() + (seconds * 1000));
	}

}
