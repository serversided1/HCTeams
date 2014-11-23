package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class LastJoinMap extends RedisPersistMap<Long> {

    public LastJoinMap() {
        super("LastJoin");
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    public void setLastJoin(String player) {
        updateValueAsync(player, System.currentTimeMillis());
    }

    public long getLastJoin(String player) {
        return (contains(player) ? getValue(player) : 0L);
    }

}