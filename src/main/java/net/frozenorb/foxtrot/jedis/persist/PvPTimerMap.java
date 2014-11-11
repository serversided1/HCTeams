package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class PvPTimerMap extends RedisPersistMap<Long> {

    public static final long PENDING_USE = -10L;

	public PvPTimerMap() {
		super("PvPTimers");
	}

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    public void pendingTimer(String player){
        updateValueAsync(player, PENDING_USE);
    }

	public void createTimer(String player, int seconds) {
		updateValueAsync(player, System.currentTimeMillis() + (seconds * 1000));
	}

	public boolean hasTimer(String player) {
        if (contains(player)) {
            if (getValue(player) == PENDING_USE) {
                return (false);
            }

            return (getValue(player) > System.currentTimeMillis());
        }

		return (false);
	}

    public long getTimer(String player) {
        return (contains(player) ? getValue(player) : -1L);
    }

    public void removeTimer(String player) {
        updateValueAsync(player, -1L);
    }

    public boolean contains(String player) {
        return (super.contains(player));
    }

    // MAP 0.9
    @Override
    public Long getValue(String player) {
        return ((long) -1);
    }

}