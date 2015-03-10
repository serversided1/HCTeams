package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.Date;
import java.util.UUID;

public class DeathbanMap extends PersistMap<Long> {

    public DeathbanMap() {
        super("Deathbans", "Deathban");
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

    public boolean isDeathbanned(UUID check) {
        if (getValue(check) != null) {
            return (getValue(check) > System.currentTimeMillis());
        }

        return (false);
    }

    public void deathban(UUID update, long seconds) {
        updateValue(update, System.currentTimeMillis() + (seconds * 1000));
    }

    public void revive(UUID update) {
        updateValue(update, 0L);
    }

    public long getDeathban(UUID check) {
        return (contains(check) ? getValue(check) : 0L);
    }

    public void wipeDeathbans() {
        wipeValues();
    }

}