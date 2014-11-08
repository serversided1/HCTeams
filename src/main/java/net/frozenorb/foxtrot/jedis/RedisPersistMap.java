package net.frozenorb.foxtrot.jedis;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public abstract class RedisPersistMap<T> {

	private HashMap<String, T> wrappedMap = new HashMap<String, T>();

	@NonNull private String keyPrefix;

	public void loadFromRedis() {
        JedisCommand<Object> jdc = new JedisCommand<Object>() {

			public Object execute(Jedis jedis) {
                Map<String, String> results = jedis.hgetAll(keyPrefix);

                for (Map.Entry<String, String> resultEntry : results.entrySet()) {
                    wrappedMap.put(resultEntry.getKey(), getJavaObject(resultEntry.getValue()));
                }

				return (null);
			}

		};

		FoxtrotPlugin.getInstance().runJedisCommand(jdc);
	}

	protected void updateValue(final String key, T value) {
		wrappedMap.put(key.toLowerCase(), value);

        JedisCommand<Object> jdc = new JedisCommand<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                jedis.hset(keyPrefix, key.toLowerCase(), getRedisValue(getValue(key)));
                return (null);
            }
        };

        FoxtrotPlugin.getInstance().runJedisCommand(jdc);
	}

    protected void updateValueAsync(final String key, T value) {
        wrappedMap.put(key.toLowerCase(), value);

        JedisCommand<Object> jdc = new JedisCommand<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                jedis.hset(keyPrefix, key.toLowerCase(), getRedisValue(getValue(key)));
                return (null);
            }
        };

        new BukkitRunnable() {

            public void run() {
                FoxtrotPlugin.getInstance().runJedisCommand(jdc);
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
    }

	protected T getValue(String key) {
		return (wrappedMap.get(key.toLowerCase()));
	}

	protected boolean contains(String key) {
		return (wrappedMap.containsKey(key.toLowerCase()));
	}

	public abstract String getRedisValue(T t);

	public abstract T getJavaObject(String str);

}