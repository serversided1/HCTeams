package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

import java.util.HashMap;
import java.util.Map;

public class PlaytimeMap extends RedisPersistMap<Long> {

    private Map<String, Long> joinDate = new HashMap<String, Long>();

    public PlaytimeMap() {
        super("PlayerPlaytimes");
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    public void playerJoined(String player) {
        joinDate.put(player, System.currentTimeMillis());

        if (!contains(player)) {
            updateValue(player, 0L);
        }
    }

    public void playerQuit(String player, boolean async) {
        if (async) {
            updateValueAsync(player, getPlaytime(player) + (System.currentTimeMillis() - joinDate.get(player)) / 1000);
        } else {
            updateValue(player, getPlaytime(player) + (System.currentTimeMillis() - joinDate.get(player)) / 1000);
        }
    }

    public long getCurrentSession(String player) {
        if (joinDate.containsKey(player)) {
            return (System.currentTimeMillis() - joinDate.get(player));
        }

        return (0L);
    }

    public long getPlaytime(String player) {
        return (contains(player) ? getValue(player) : 0L);
    }

}