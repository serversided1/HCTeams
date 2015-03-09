package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.Date;
import java.util.UUID;

public class OppleMap extends PersistMap<Long> {

    public OppleMap() {
        super("OppleCooldowns", "OppleCooldown");
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

    public boolean isOnCooldown(UUID check) {
        return (getValue(check.toString()) != null && getValue(check.toString()) > System.currentTimeMillis());
    }

    public void useGoldenApple(UUID update, long seconds) {
        updateValueAsync(update.toString(), System.currentTimeMillis() + (seconds * 1000));
    }

    public void resetCooldown(UUID update) {
        updateValueAsync(update.toString(), 0L);
    }

    public long getCooldown(UUID check) {
        return (contains(check.toString()) ? getValue(check.toString()) : -1L);
    }

}