package net.frozenorb.foxtrot.jedis;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public abstract class RedisPersistMap<T> {

    private Map<String, T> wrappedMap = new ConcurrentHashMap<String, T>();

    @NonNull private String keyPrefix;
    @NonNull private String mongoKeyPrefix;

    public void loadFromRedis() {
        JedisCommand<Object> jdc = new JedisCommand<Object>() {

            public Object execute(Jedis jedis) {
                Map<String, String> results = jedis.hgetAll(keyPrefix);

                for (Map.Entry<String, String> resultEntry : results.entrySet()) {
                    T object = getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());

                    if (object != null) {
                        wrappedMap.put(resultEntry.getKey(), object);
                    }
                }

                return (null);
            }

        };

        FoxtrotPlugin.getInstance().runJedisCommand(jdc);
    }

    protected void wipeValues() {
        wrappedMap.clear();

        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

            public Object execute(Jedis jedis) {
                jedis.del(keyPrefix);
                return (null);
            }

        });
    }

    public void reloadValue(final String key) {
        JedisCommand<Object> jdc = new JedisCommand<Object>() {

            public Object execute(Jedis jedis) {
                String result = jedis.hget(keyPrefix, key);

                if (result != null) {
                    T object = getJavaObjectSafe(key, result);

                    if (object != null) {
                        wrappedMap.put(key, object);
                    }
                }

                return (null);
            }

        };

        FoxtrotPlugin.getInstance().runJedisCommand(jdc);
    }

    protected void updateValue(final String key, final T value) {
        wrappedMap.put(key.toLowerCase(), value);

        JedisCommand<Object> jdc = new JedisCommand<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                jedis.hset(keyPrefix, key.toLowerCase(), getRedisValue(getValue(key)));
                DBCollection playersCollection = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Players");

                playersCollection.update(new BasicDBObject("_id", key), new BasicDBObject("$set", new BasicDBObject(mongoKeyPrefix, getMongoValue(getValue(key)))), true, false);

                return (null);
            }
        };

        FoxtrotPlugin.getInstance().runJedisCommand(jdc);
    }

    protected void updateValueAsync(final String key, T value) {
        wrappedMap.put(key.toLowerCase(), value);

        final JedisCommand<Object> jdc = new JedisCommand<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                jedis.hset(keyPrefix, key.toLowerCase(), getRedisValue(getValue(key)));
                DBCollection playersCollection = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Players");

                playersCollection.update(new BasicDBObject("_id", key), new BasicDBObject("$set", new BasicDBObject(mongoKeyPrefix, getMongoValue(getValue(key)))), true, false);

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

    public boolean contains(String key) {
        return (wrappedMap.containsKey(key.toLowerCase()));
    }

    public abstract String getRedisValue(T t);

    public abstract Object getMongoValue(T t);

    public T getJavaObjectSafe(String key, String redisValue) {
        try {
            return (getJavaObject(redisValue));
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            System.out.println("Error parsing Redis result.");
            System.out.println(" - Prefix: " + keyPrefix);
            System.out.println(" - Key: " + key);
            System.out.println(" - Value: " + redisValue);
            return (null);
        }
    }

    public abstract T getJavaObject(String str);

}