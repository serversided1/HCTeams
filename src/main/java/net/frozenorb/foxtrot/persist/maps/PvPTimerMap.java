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
        updateValueAsync(update.toString(), PENDING_USE);
    }

    public void createTimer(UUID update, int seconds) {
        updateValueAsync(update.toString(), System.currentTimeMillis() + (seconds * 1000));
    }

    public boolean hasTimer(UUID check) {
        if (contains(check.toString())) {
            return (getValue(check.toString()) != PENDING_USE && getValue(check.toString()) > System.currentTimeMillis());
        }

        return (false);
    }

    public long getTimer(UUID check) {
        return (contains(check.toString()) ? getValue(check.toString()) : -1L);
    }

    public void removeTimer(UUID update) {
        updateValueAsync(update.toString(), -1L);
    }

    public boolean contains(UUID check) {
        return (contains(check.toString()));
    }

    @Override
    public Long getValue(String string) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() || FoxtrotPlugin.getInstance().getMapHandler().isKitMap()) {
            return (-1L);
        }

        return (super.getValue(string));
    }

}