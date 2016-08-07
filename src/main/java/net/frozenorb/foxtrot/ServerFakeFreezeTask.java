package net.frozenorb.foxtrot;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public final class ServerFakeFreezeTask extends BukkitRunnable {

    public static final double SPIKE_MIN_MOD = 2.5;
    public static final double STABLE_MIN_MOD = 1.6;
    public static final long STABLE_MIN_TIME = TimeUnit.MINUTES.toMillis(3);
    public static final int MIN_PLAYERS_TO_FREEZE = 50;

    private RollingAverage oneMinLatencyAvg = new RollingAverage(60);
    private double oneMinLatencyAvgBeforeFreeze = 0;
    @Getter private static long okLatencyResumed = -1;
    @Getter private static boolean frozen = false;

    @Override
    public void run() {
        double currentLatencyAvg = avgLatency();
        oneMinLatencyAvg.add(currentLatencyAvg);

        if (playerCount() < MIN_PLAYERS_TO_FREEZE) {
            if (frozen) {
                unfreeze();
            }

            return;
        }

        if (frozen) {
            boolean stable = currentLatencyAvg <= oneMinLatencyAvgBeforeFreeze * STABLE_MIN_MOD;

            if (stable) {
                if (okLatencyResumed < 0) {
                    okLatencyResumed = System.currentTimeMillis();
                }

                if (System.currentTimeMillis() - okLatencyResumed > STABLE_MIN_TIME) {
                    oneMinLatencyAvgBeforeFreeze = 0;
                    okLatencyResumed = -1;
                    unfreeze();
                }
            } else {
                okLatencyResumed = -1;
            }
        } else {
            boolean spikeDetected = currentLatencyAvg >= oneMinLatencyAvg.getAverage() * SPIKE_MIN_MOD;

            if (spikeDetected) {
                oneMinLatencyAvgBeforeFreeze = oneMinLatencyAvg.getAverage();
                okLatencyResumed = -1;
                freeze();
            }
        }
    }

    private double avgLatency() {
        int totalLatency = 0;
        int measurements = 0;

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            int playerLatency = ((CraftPlayer) player).getHandle().ping;

            if (playerLatency <= 100_000) {
                totalLatency += playerLatency;
                measurements++;
            }
        }

        return measurements == 0 ? 0 : totalLatency / measurements;
    }

    private int playerCount() {
        return Foxtrot.getInstance().getServer().getOnlinePlayers().size();
    }

    private void freeze() {
        frozen = true;

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Foxtrot autofreeze would have frozen the server.");
            }
        }

        Foxtrot.getInstance().getLogger().info("Foxtrot autofreeze would have frozen the server.");
    }

    private void unfreeze() {
        frozen = false;

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Foxtrot autofreeze would have unfrozen the server.");
            }
        }

        Foxtrot.getInstance().getLogger().info("Foxtrot autofreeze would have unfrozen the server.");
    }

    public static class RollingAverage {

        private int size;
        private double total = 0D;
        private int index = 0;
        private double samples[];

        public RollingAverage(int size) {
            this.size = size;
            samples = new double[size];
            for (int i = 0; i < size; i++) samples[i] = 0d;
        }

        public void add(double x) {
            total -= samples[index];
            samples[index] = x;
            total += x;
            if (++index == size) index = 0; // cheaper than modulus
        }

        public double getAverage() {
            return total / size;
        }

    }

}