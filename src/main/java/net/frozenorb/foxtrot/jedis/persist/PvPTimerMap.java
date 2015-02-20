package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.RedisPersistMap;

import java.util.Date;

public class PvPTimerMap extends RedisPersistMap<Long> {

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

    @Override
    public Long getValue(String player) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() || FoxtrotPlugin.getInstance().getMapHandler().isKitMap()) {
            return (-1L);
        }

        return (super.getValue(player));
    }

}