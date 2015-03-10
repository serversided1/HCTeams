package net.frozenorb.foxtrot.persist;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public abstract class PersistMap<T> {

    private Map<UUID, T> wrappedMap = new ConcurrentHashMap<>();

    @NonNull private String keyPrefix;
    @NonNull private String mongoKeyPrefix;

    public void loadFromRedis() {
        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

            public Object execute(Jedis jedis) {
                Map<String, String> results = jedis.hgetAll(keyPrefix);

                for (Map.Entry<String, String> resultEntry : results.entrySet()) {
                    T object = getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());

                    if (object != null) {
                        wrappedMap.put(UUID.fromString(resultEntry.getKey()), object);
                    }
                }

                return (null);
            }

        });
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

    protected void updateValue(final UUID key, final T value) {
        wrappedMap.put(key, value);

        JedisCommand<Object> jdc = new JedisCommand<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                jedis.hset(keyPrefix, key.toString(), getRedisValue(getValue(key)));
                DBCollection playersCollection = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Players");

                playersCollection.update(new BasicDBObject("_id", key.toString()), new BasicDBObject("$set", new BasicDBObject(mongoKeyPrefix, getMongoValue(getValue(key)))), true, false);

                return (null);
            }
        };

        FoxtrotPlugin.getInstance().runJedisCommand(jdc);
    }

    protected void updateValueAsync(final UUID key, T value) {
        wrappedMap.put(key, value);

        new BukkitRunnable() {

            public void run() {
                FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

                    @Override
                    public Object execute(Jedis jedis) {
                        jedis.hset(keyPrefix, key.toString(), getRedisValue(getValue(key)));
                        DBCollection playersCollection = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Players");

                        playersCollection.update(new BasicDBObject("_id", key.toString()), new BasicDBObject("$set", new BasicDBObject(mongoKeyPrefix, getMongoValue(getValue(key)))), true, false);

                        return (null);
                    }
                });
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
    }

    protected T getValue(UUID key) {
        return (wrappedMap.get(key));
    }

    protected boolean contains(UUID key) {
        return (wrappedMap.containsKey(key));
    }

    public abstract String getRedisValue(T t);

    public abstract Object getMongoValue(T t);

    public T getJavaObjectSafe(String key, String redisValue) {
        try {
            return (getJavaObject(redisValue));
        } catch (Exception e) {
            System.out.println("Error parsing Redis result.");
            System.out.println(" - Prefix: " + keyPrefix);
            System.out.println(" - Key: " + key);
            System.out.println(" - Value: " + redisValue);
            return (null);
        }
    }

    public abstract T getJavaObject(String str);

}