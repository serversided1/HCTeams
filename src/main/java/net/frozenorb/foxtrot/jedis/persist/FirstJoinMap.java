package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

import java.util.Date;

public class FirstJoinMap extends RedisPersistMap<Long> {

    public FirstJoinMap() {
        super("FirstJoin", "FirstJoined");
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

    public void setFirstJoin(String player) {
        updateValueAsync(player, System.currentTimeMillis());
    }

    public long getFirstJoin(String player) {
        return (contains(player) ? getValue(player) : 0L);
    }

}