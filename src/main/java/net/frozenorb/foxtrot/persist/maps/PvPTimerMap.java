package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRBitmaskType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.UUID;

public class PvPTimerMap extends PersistMap<Long> {

    // All the weird casts to int are because this is (mid map 4) stored as a long, and we can't really change that. Map 5 we'll fix this.
    public static final int PENDING_USE = -10;

    public PvPTimerMap() {
        super("PvPTimers", "PvPTimer");

        // This should probably use a bit smarter of a system... but for now it's fine.
        new BukkitRunnable() {

            public void run() {
                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    if (hasActiveTimer(player.getUniqueId())) {
                        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                            continue;
                        }

                        updateValueAsync(player.getUniqueId(), getValue(player.getUniqueId()) - 1);
                    }
                 }
            }

        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        long parsed = Long.parseLong(str);
        long parsedOrig = parsed;

        // Converter
        if (parsed == -1) {
            parsed = 0;
        } else if (parsed > 30 * 60) {
            long millisLeft = parsed - System.currentTimeMillis();
            parsed = millisLeft / 1000L;

            if (parsed < 20) {
                parsed = 0;
            }
        }

        if (parsedOrig != parsed && parsed != 0) {
            Foxtrot.getInstance().getLogger().info("[PvP Timer Converter] Converted entry. Original: " + parsedOrig + ", New: " + parsed);
        }

        return (parsed);
    }

    @Override
    public Object getMongoValue(Long time) {
        return (new Date(time));
    }

    public void removeTimer(UUID update) {
        updateValueAsync(update, 0L);
    }

    public void createPendingTimer(UUID update) {
        updateValueAsync(update, (long) PENDING_USE);
    }

    public boolean hasPendingTimer(UUID check) {
        if (Foxtrot.getInstance().getServerHandler().isPreEOTW() || Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return (false);
        }

        return (getValue(check) == PENDING_USE);
    }

    public void createActiveTimer(UUID update, int seconds) {
        updateValueAsync(update, (long) seconds);
    }

    public boolean hasActiveTimer(UUID check) {
        if (Foxtrot.getInstance().getServerHandler().isPreEOTW() || Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return (false);
        }

        if (contains(check)) {
            return (getValue(check) > 0);
        }

        return (false);
    }

    public int getSecondsRemaining(UUID check) {
        if (Foxtrot.getInstance().getServerHandler().isPreEOTW() || Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return (0);
        }

        return (contains(check) ? getValue(check).intValue() : 0);
    }

}