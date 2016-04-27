package net.frozenorb.foxtrot.map.kit.stats.listener;

import com.google.common.collect.Maps;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.stats.StatsEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;
import java.util.UUID;

public class StatsListener implements Listener {

    private Map<UUID, UUID> lastKilled = Maps.newHashMap();
    private Map<UUID, Integer> boosting = Maps.newHashMap();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer.equals(victim) || isNaked(victim) || (boosting.containsKey(killer.getUniqueId()) && boosting.get(killer.getUniqueId()) > 2)) {
            return;
        }

        if (lastKilled.containsKey(killer.getUniqueId()) && lastKilled.get(killer.getUniqueId()) == victim.getUniqueId()) {
            boosting.putIfAbsent(killer.getUniqueId(), 0);
            boosting.put(killer.getUniqueId(), boosting.get(killer.getUniqueId()) + 1);
        } else {
            boosting.put(killer.getUniqueId(), 0);
        }

        StatsEntry victimStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(victim);
        StatsEntry killerStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(killer);

        victimStats.addDeath();
        killerStats.addKill();

        lastKilled.put(killer.getUniqueId(), victim.getUniqueId());
    }

    private boolean isNaked(Player player) {
        return player.getInventory().getHelmet() == null &&
                player.getInventory().getChestplate() == null &&
                player.getInventory().getLeggings() == null &&
                player.getInventory().getBoots() == null;
    }

}
