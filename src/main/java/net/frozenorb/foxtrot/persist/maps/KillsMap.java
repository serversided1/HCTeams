package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class KillsMap extends PersistMap<Integer> {

    public KillsMap() {
        super("Kills", "Kills");
    }

    @Override
    public String getRedisValue(Integer kills) {
        return (String.valueOf(kills));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer kills) {
        return (kills);
    }

    public int getKills(UUID check) {
        return (contains(check.toString()) ? getValue(check.toString()) : 0);
    }

    public void setKills(UUID update, int kills) {
        updateValueAsync(update.toString(), kills);
    }

}