package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PvPTimerMap extends PersistMap<Integer> {

    public PvPTimerMap() {
        super("PvPTimers", "PvPTimer", false); // dont save this data to mongo
        // This should probably use a bit smarter of a system... but for now it's fine.
        new BukkitRunnable() {

            public void run() {
                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    if (hasTimer(player.getUniqueId())) {
                        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                            continue;
                        }

                        int newValue = getValue(player.getUniqueId()) - 1;

                        if (newValue % 60 == 0) {
                            player.sendMessage(ChatColor.RED + "You have " + ChatColor.BOLD + (newValue / 60) + ChatColor.RED + " minutes of PvP Protection remaining.");
                        }

                        updateValueAsync(player.getUniqueId(), newValue);
                    }
                 }
            }

        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
    }

    @Override
    public String getRedisValue(Integer time) {
        return (String.valueOf(time));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer time) {
        return (time);
    }

    public void removeTimer(UUID update) {
        updateValueAsync(update, 0);
    }

    public void createTimer(UUID update, int seconds) {
        updateValueAsync(update, seconds);
    }

    public boolean hasTimer(UUID check) {
        return (getSecondsRemaining(check) > 0);
    }

    public int getSecondsRemaining(UUID check) {
        if (Foxtrot.getInstance().getServerHandler().isPreEOTW() || Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return (0);
        }

        return (contains(check) ? getValue(check) : 0);
    }

}