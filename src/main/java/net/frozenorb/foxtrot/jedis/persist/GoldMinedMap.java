package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class GoldMinedMap extends RedisPersistMap<Integer> {

    public GoldMinedMap() {
        super("GoldMined", "MiningStats.Gold");
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
    public Object getMongoValue(Integer mined) {
        return (mined);
    }

    public int getMined(String player) {
        return (contains(player) ? getValue(player) : 0);
    }

    public void setMined(String player, int mined) {
        updateValueAsync(player, mined);
    }

}