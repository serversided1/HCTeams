package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.queue.PlayerInfoPacket;

import java.util.*;

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
        updateValueAsync(update, System.currentTimeMillis() + (seconds * 1000));
    }

    public void revive(UUID update) {
        updateValueAsync(update, 0L);
        PlayerInfoPacket.sendResponse(update);
    }

    public long getDeathban(UUID check) {
        return (contains(check) ? getValue(check) : 0L);
    }

    public void wipeDeathbans() {
        wipeValues();
    }

    public Collection<UUID> getDeathbannedPlayers() {
        Collection<UUID> deathbannedPlayers = new HashSet<>();

        for (Map.Entry<UUID, Long> entry : wrappedMap.entrySet()) {
            if (isDeathbanned(entry.getKey())) {
                deathbannedPlayers.add(entry.getKey());
            }
        }

        return (deathbannedPlayers);
    }

}