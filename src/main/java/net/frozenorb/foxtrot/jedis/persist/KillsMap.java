package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class KillsMap extends RedisPersistMap<Integer> {

	public KillsMap() {
		super("Kills");
	}

    @Override
    public String getRedisValue(Integer kills) {
        return (String.valueOf(kills));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    public int getKills(String player) {
        return (contains(player) ? getValue(player) : 0);
    }

    public void setKills(String player, int kills) {
        updateValueAsync(player, kills);
    }

}