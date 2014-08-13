package net.frozenorb.foxtrot.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import redis.clients.jedis.Jedis;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class RedisPersistMap<T> {

	private HashMap<String, T> wrappedMap = new HashMap<String, T>();
	private HashSet<String> updatedKeys = new HashSet<String>();

	@NonNull private String keyPrefix;

	/**
	 * Loads the contents of the internal map from the according key-value pairs
	 * in redis with the same prefix.
	 */
	public final void loadFromRedis() {
		JedisCommand<Object> jdc = new JedisCommand<Object>() {

			@Override
			public Object execute(Jedis jedis) {
				for (String key : jedis.keys(keyPrefix + ".*")) {
					String storedKey = key.substring(key.indexOf('.') + 1);

					if (jedis.get(key) != null) {
						wrappedMap.put(storedKey, getJavaObject(jedis.get(key)));
					}
				}

				return null;
			}
		};

		FoxtrotPlugin.getInstance().runJedisCommand(jdc);

	}

	/**
	 * Asynchronously writes the key-value pairs that need updating to redis.
	 */
	public void saveToRedis() {
		JedisCommand<Integer> jdc = new JedisCommand<Integer>() {

			@Override
			public Integer execute(Jedis jedis) {

				int done = 0;

				for (String key : updatedKeys) {
					T toSave = getValue(key);

					jedis.set(keyPrefix + '.' + key.toLowerCase(), getRedisValue(toSave));
					done++;
				}

				updatedKeys.clear();

				return done;
			}
		};

		int done = FoxtrotPlugin.getInstance().runJedisCommand(jdc);

		System.out.println("Successfully saved " + done + " key-value pairs with prefix '" + keyPrefix + "'");
	}

	public void executeSave(Jedis jedis) {

		int done = 0;

		for (String key : updatedKeys) {
			T toSave = getValue(key);

			jedis.set(keyPrefix + '.' + key.toLowerCase(), getRedisValue(toSave));
			done++;
		}

		updatedKeys.clear();

		System.out.println("Successfully saved " + done + " key-value pairs with prefix '" + keyPrefix + "'");

	}

	public void updateValue(String key, T value) {
		wrappedMap.put(key.toLowerCase(), value);
		updatedKeys.add(key.toLowerCase());
		saveToRedis();
	}

	public T getValue(String key) {
		return wrappedMap.get(key.toLowerCase());
	}

	public List<String> keyList() {
		return new ArrayList<String>(wrappedMap.keySet());
	}

	/**
	 * Gets the String that will be stored as the value instead of the given
	 * Java object.
	 * 
	 * @param t
	 *            the Java object to get the string value of
	 * @return string value
	 */
	public abstract String getRedisValue(T t);

	/**
	 * Gets the Java object represented by the String value in redis
	 * 
	 * @param str
	 *            string value as recalled from redis
	 * @return object
	 */
	public abstract T getJavaObject(String str);

}
