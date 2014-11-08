package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

import java.util.concurrent.TimeUnit;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class LastDeathMap extends RedisPersistMap<Long> {

    public LastDeathMap() {
        super("LastDeaths");
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    public boolean recentlyDied(String player) {
        if (getValue(player) != null) {
            return ((getValue(player) - System.currentTimeMillis()) > TimeUnit.MINUTES.toMillis(15));
        }

        return (false);
    }

    public void addDeath(String player) {
        updateValue(player, System.currentTimeMillis());
    }

    public long getLastDeath(String player) {
        return (contains(player) ? getValue(player) : 0L);
    }

}