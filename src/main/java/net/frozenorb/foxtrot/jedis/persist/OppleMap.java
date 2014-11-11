package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class OppleMap extends RedisPersistMap<Long> {

	public OppleMap() {
		super("OppleCooldowns");
	}

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    public boolean isOnCooldown(String player) {
        if (getValue(player) != null) {
            return (getValue(player) > System.currentTimeMillis());
        }

        return (false);
    }

    public void useGoldenApple(String player, long seconds) {
        updateValueAsync(player.toLowerCase(), System.currentTimeMillis() + (seconds * 1000));
    }

    public void resetCooldown(String player) {
        updateValueAsync(player, 0L);
    }

    public long getCooldown(String player) {
        return (getValue(player));
    }

}