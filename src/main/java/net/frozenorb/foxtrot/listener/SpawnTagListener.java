package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.util.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class SpawnTagListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = PlayerUtils.getDamageSource(event.getDamager());

        /* Only tag player damagers, and deny tagging self */
        if (damager != null && damager != event.getEntity()) {
            SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime());
            SpawnTagHandler.addPassiveSeconds((Player) event.getEntity(), SpawnTagHandler.getMaxTagTime());
        }
    }

}