package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

import java.util.Date;

public class LastJoinMap extends RedisPersistMap<Long> {

    public LastJoinMap() {
        super("LastJoin", "LastJoined");
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    @Override
    public Object getMongoValue(Long time) {
        return (new Date(time));
    }

    public void setLastJoin(String player) {
        updateValueAsync(player, System.currentTimeMillis());
    }

    public long getLastJoin(String player) {
        return (contains(player) ? getValue(player) : 0L);
    }

}