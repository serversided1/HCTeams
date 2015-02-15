package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class TransferableLivesMap extends RedisPersistMap<Integer> {

    public TransferableLivesMap() {
        super("TransferableLives");
    }

    @Override
    public String getRedisValue(Integer lives) {
        return (String.valueOf(lives));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer lives) {
        return (lives);
    }

    public int getLives(String player) {
        return (contains(player) ? getValue(player) : 0);
    }

    public void setLives(String player, int lives) {
        updateValue(player, lives);
    }

}