package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class SoulboundLivesMap extends RedisPersistMap<Integer> {

    public SoulboundLivesMap() {
        super("SoulboundLives");
    }

    @Override
    public String getRedisValue(Integer lives) {
        return (String.valueOf(lives));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    public int getLives(String player) {
        return (contains(player) ? getValue(player) : 0);
    }

    public void setLives(String player, int lives) {
        updateValue(player, lives);
    }

    @Override
    public Integer getValue(String key) {
        reloadValue(key);
        return (super.getValue(key));
    }

}