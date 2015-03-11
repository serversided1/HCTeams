package net.frozenorb.foxtrot.util;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.persist.JedisCommand;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UUIDCache {

    private static Map<UUID, String> uuidToName = new ConcurrentHashMap<>();
    private static Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();

    private UUIDCache() {}

    public static void load() {
        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Void>() {

            @Override

            public Void execute(Jedis jedis) {
                Map<String, String> cache = jedis.hgetAll("UUIDCache");

                for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                    update(UUID.fromString(cacheEntry.getKey()), cacheEntry.getValue(), false);
                }

                return (null);
            }

        });
    }

    public static UUID uuid(String name) {
        return (nameToUuid.get(name));
    }

    public static String name(UUID uuid) {
        return (uuidToName.get(uuid));
    }

    public static void ensure(UUID uuid) {
        if (name(uuid) != null) {
            update(uuid, FoxtrotPlugin.getInstance().getServer().getOfflinePlayer(uuid).getName(), true);
        }
    }

    public static void update(UUID uuid, String name, boolean redis) {
        uuidToName.put(uuid, name);

        // Flush any old entries out of the cache.
        nameToUuid.entrySet().removeIf(entry -> entry.getValue().equals(uuid));
        nameToUuid.put(name, uuid);

        if (redis) {
            new BukkitRunnable() {

                public void run() {
                    FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Void>() {

                        @Override
                        public Void execute(Jedis jedis) {
                            jedis.hset("UUIDCache", uuid.toString(), name);
                            return (null);
                        }

                    });
                }

            }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
        }
    }

}