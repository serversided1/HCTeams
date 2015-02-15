package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class FishingKitMap extends RedisPersistMap<Integer> {

    public FishingKitMap() {
        super("FishingKitUses");
    }

    @Override
    public String getRedisValue(Integer uses) {
        return (String.valueOf(uses));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer uses) {
        return (uses);
    }

    public int getUses(String player) {
        return (contains(player) ? getValue(player) : 0);
    }

    public void setUses(String player, int uses) {
        updateValueAsync(player, uses);
    }

}