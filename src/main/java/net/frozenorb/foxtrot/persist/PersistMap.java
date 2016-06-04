package net.frozenorb.foxtrot.persist;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.redis.RedisCommand;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PersistMap<T> {

    protected Map<UUID, T> wrappedMap = new ConcurrentHashMap<>();

    private String keyPrefix;
    private String mongoName;
    private boolean useMongo;

    public PersistMap(String keyPrefix, String mongoName) {
        this(keyPrefix, mongoName, true); // use mongo by default
    }

    public PersistMap(String keyPrefix, String mongoName, boolean useMongo) {
        this.keyPrefix = keyPrefix;
        this.mongoName = mongoName;
        this.useMongo = useMongo;

        loadFromRedis();
    }

    public void loadFromRedis() {
        qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                Map<String, String> results = redis.hgetAll(keyPrefix);

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

        qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                redis.del(keyPrefix);
                return (null);
            }

        });
    }

    protected void updateValueSync(final UUID key, final T value) {
        wrappedMap.put(key, value);

        qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                redis.hset(keyPrefix, key.toString(), getRedisValue(getValue(key)));

                if (useMongo) {
                    DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
                    BasicDBObject player = new BasicDBObject("_id", key.toString().replace("-", ""));

                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject(mongoName, getMongoValue(getValue(key)))), true, false);
                }
                return (null);
            }

        });
    }

    protected void updateValueAsync(final UUID key, T value) {
        wrappedMap.put(key, value);

        new BukkitRunnable() {

            public void run() {
                qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

                    @Override
                    public Object execute(Jedis redis) {
                        redis.hset(keyPrefix, key.toString(), getRedisValue(getValue(key)));

                        if (useMongo) {
                            DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
                            BasicDBObject player = new BasicDBObject("_id", key.toString().replace("-", ""));

                            playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject(mongoName, getMongoValue(getValue(key)))), true, false);
                        }
                        return (null);
                    }

                });
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
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