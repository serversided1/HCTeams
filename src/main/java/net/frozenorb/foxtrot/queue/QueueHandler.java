package net.frozenorb.foxtrot.queue;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.HashSet;
import java.util.Set;

public class QueueHandler extends BukkitRunnable {

    public static final String QUEUE_PREFIX = "HCTQueue:";
    public static final String SERVER_NAME = "foxtrot";

    @Getter private Set<String> moving = new HashSet<>();

    public QueueHandler() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new QueueListener(), Foxtrot.getInstance());
        this.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
    }

    public void run() {
        Jedis redis = Foxtrot.getInstance().getQueuePool().getResource();

        try {
            if (!Boolean.parseBoolean(redis.get(QUEUE_PREFIX + "QueueEnabled"))) {
                return;
            }

            int requested = countOpenSlots();
            Set<String> toSend = redis.zrange(QUEUE_PREFIX + "Queue", 0, requested);

            for (final String queuedPlayer : toSend) {
                if (!canMovePlayer(queuedPlayer) || moving.contains(queuedPlayer)) {
                    continue;
                }

                Foxtrot.getInstance().getLogger().info("Moving " + queuedPlayer + " to HCTeams...");

                //redis.sadd(QUEUE_PREFIX + "Moving", queuedPlayer); // So the Bungees let them move.
                //redis.publish("redisbungee-allservers", "/send " + queuedPlayer + " " + SERVER_NAME);
                moving.add(queuedPlayer);

                new BukkitRunnable() {

                    public void run() {
                        if (moving.remove(queuedPlayer)) {
                            Foxtrot.getInstance().getLogger().warning("Player " + queuedPlayer + " didn't join when the queue was expecting them to!");
                        }
                    }

                }.runTaskLater(Foxtrot.getInstance(), 100L);
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (redis != null) {
                Foxtrot.getInstance().getQueuePool().returnBrokenResource(redis);
                redis = null;
            }
        } finally {
            if (redis != null) {
                Foxtrot.getInstance().getQueuePool().returnResource(redis);
            }
        }
    }

    public int countOpenSlots() {
        int open = 5; // TODO
        return (Math.max(open - moving.size(), 0));
    }

    public boolean canMovePlayer(String player) {
        if (Foxtrot.getInstance().getServer().hasWhitelist() && !Foxtrot.getInstance().getServer().getOfflinePlayer(player).isWhitelisted()) {
            return (false);
        }

        return (true);
    }

}