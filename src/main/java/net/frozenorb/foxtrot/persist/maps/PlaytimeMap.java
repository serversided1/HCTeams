package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlaytimeMap extends PersistMap<Long> {

    private Map<UUID, Long> joinDate = new HashMap<>();

    public PlaytimeMap() {
        super("PlayerPlaytimes", "Playtime");
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
        return (time.intValue());
    }

    public void playerJoined(UUID update) {
        joinDate.put(update, System.currentTimeMillis());

        if (!contains(update.toString())) {
            updateValueAsync(update.toString(), 0L);
        }
    }

    public void playerQuit(UUID update, boolean async) {
        if (async) {
            updateValueAsync(update.toString(), getPlaytime(update) + (System.currentTimeMillis() - joinDate.get(update)) / 1000);
        } else {
            updateValue(update.toString(), getPlaytime(update) + (System.currentTimeMillis() - joinDate.get(update)) / 1000);
        }
    }

    public long getCurrentSession(UUID check) {
        if (joinDate.containsKey(check)) {
            return (System.currentTimeMillis() - joinDate.get(check));
        }

        return (0L);
    }

    public long getPlaytime(UUID check) {
        return (contains(check.toString()) ? getValue(check.toString()) : 0L);
    }

    public boolean hasPlayed(UUID check) {
        return (contains(check.toString()));
    }

    public void setPlaytime(UUID update, long playtime) {
        updateValue(update.toString(), playtime);
    }

}