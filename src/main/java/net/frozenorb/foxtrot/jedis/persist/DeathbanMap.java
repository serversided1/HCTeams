package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class DeathbanMap extends RedisPersistMap<Long> {

	public DeathbanMap() {
		super("Deathbans");
	}

	@Override
	public String getRedisValue(Long time) {
		return (String.valueOf(time));
	}

	@Override
	public Long getJavaObject(String str) {
		return (Long.parseLong(str));
	}

	public boolean isDeathbanned(String player) {
        if (getValue(player) != null) {
            if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
                // Ignore deathbans less than 5 days (at EOTW we deathban for 10 days)
                return ((getValue(player) - System.currentTimeMillis()) > (1000L * 60 * 60 * 24 * 5));
            } else {
                return (getValue(player) > System.currentTimeMillis());
            }
        }

        return (false);
	}

	public void deathban(String player, long seconds) {
		updateValueAsync(player, System.currentTimeMillis() + (seconds * 1000));
        FoxtrotPlugin.getInstance().getLastDeathMap().addDeath(player);
	}

    public void revive(String player) {
        updateValueAsync(player, 0L);
    }

    public long getDeathban(String player) {
        return (contains(player) ? getValue(player) : 0L);
    }

}