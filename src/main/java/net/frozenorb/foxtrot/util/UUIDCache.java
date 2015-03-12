package net.frozenorb.foxtrot.util;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.persist.JedisCommand;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.command.ParameterType;
import net.frozenorb.qlib.qLib;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class UUIDCache {

    private static Map<UUID, String> uuidToName = new ConcurrentHashMap<>();
    private static Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();

    private UUIDCache() {}

    static {
        FrozenCommandHandler.registerParameterType(UUID.class, new UUIDParameterType());
    }

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
        if (!name(uuid).equals("null")) {
            FoxtrotPlugin.getInstance().getLogger().warning(uuid + " didn't have a cached Redis name.");
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

    public static class UUIDParameterType implements ParameterType<UUID> {

        public UUID transform(CommandSender sender, String source) {
            if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
                return (((Player) sender).getUniqueId());
            }

            UUID uuid = uuid(source);

            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + "No player with the name " + source + " found.");
                return (null);
            }

            return (uuid);
        }

        public List<String> tabComplete(Player sender, Set<String> flags, String source) {
            List<String> completions = new ArrayList<>();

            for (Player player : qLib.getInstance().getServer().getOnlinePlayers()) {
                if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                    completions.add(player.getName());
                }
            }

            return (completions);
        }

    }

}