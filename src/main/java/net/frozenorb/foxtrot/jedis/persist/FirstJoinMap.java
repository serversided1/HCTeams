package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class FirstJoinMap extends RedisPersistMap<Long> {

    public FirstJoinMap() {
        super("FirstJoin");
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    public void setFirstJoin(String player) {
        updateValueAsync(player, System.currentTimeMillis());
    }

    public long getFirstJoin(String player) {
        return (contains(player) ? getValue(player) : 0L);
    }

}