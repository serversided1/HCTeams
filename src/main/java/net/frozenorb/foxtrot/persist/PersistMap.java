package net.frozenorb.foxtrot.persist;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.qLib;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public abstract class PersistMap<T> {

    private Map<UUID, T> wrappedMap = new ConcurrentHashMap<>();

    @NonNull private String keyPrefix;
    @NonNull private String mongoKeyPrefix;

    public void loadFromRedis() {
        qLib.getInstance().runRedisCommand(redis -> {
            Map<String, String> results = redis.hgetAll(keyPrefix);

            for (Map.Entry<String, String> resultEntry : results.entrySet()) {
                T object = getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());

                if (object != null) {
                    wrappedMap.put(UUID.fromString(resultEntry.getKey()), object);
                }
            }

            return (null);
        });
    }

    protected void wipeValues() {
        wrappedMap.clear();

        qLib.getInstance().runRedisCommand(redis -> {
            redis.del(keyPrefix);
            return (null);
        });
    }

    protected void updateValueSync(final UUID key, final T value) {
        wrappedMap.put(key, value);

        qLib.getInstance().runRedisCommand(redis -> {
            redis.hset(keyPrefix, key.toString(), getRedisValue(getValue(key)));

            DBCollection playersCollection = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Players");
            playersCollection.update(new BasicDBObject("_id", key.toString()), new BasicDBObject("$set", new BasicDBObject(mongoKeyPrefix, getMongoValue(getValue(key)))), true, false);

            return (null);
        });
    }

    protected void updateValueAsync(final UUID key, T value) {
        wrappedMap.put(key, value);

        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runRedisCommand(redis -> {
                    redis.hset(keyPrefix, key.toString(), getRedisValue(getValue(key)));

                    DBCollection playersCollection = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Players");
                    playersCollection.update(new BasicDBObject("_id", key.toString()), new BasicDBObject("$set", new BasicDBObject(mongoKeyPrefix, getMongoValue(getValue(key)))), true, false);

                    return (null);
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