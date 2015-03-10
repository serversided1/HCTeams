package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.Date;
import java.util.UUID;

public class PvPTimerMap extends PersistMap<Long> {

    public static final long PENDING_USE = -10L;

    public PvPTimerMap() {
        super("PvPTimers", "PvPTimer");
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

    public void pendingTimer(UUID update){
        updateValueAsync(update, PENDING_USE);
    }

    public void createTimer(UUID update, int seconds) {
        updateValueAsync(update, System.currentTimeMillis() + (seconds * 1000));
    }

    public boolean hasTimer(UUID check) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW()) {
            return (false);
        }

        if (contains(check)) {
            return (getValue(check) != PENDING_USE && getValue(check) > System.currentTimeMillis());
        }

        return (false);
    }

    public long getTimer(UUID check) {
        return (contains(check) ? getValue(check) : -1L);
    }

    public void removeTimer(UUID update) {
        updateValueAsync(update, -1L);
    }

    public boolean contains(UUID check) {
        return (super.contains(check));
    }

    @Override
    public Long getValue(UUID check) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() || FoxtrotPlugin.getInstance().getMapHandler().isKitMap()) {
            return (-1L);
        }

        return (super.getValue(check));
    }

}