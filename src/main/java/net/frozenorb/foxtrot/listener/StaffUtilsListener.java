package net.frozenorb.foxtrot.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class StaffUtilsListener implements Listener {

    private static Location lastDamageLocation;

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player && !event.isCancelled()) {
            lastDamageLocation = event.getEntity().getLocation();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (lastDamageLocation != null && event.getItem() != null && event.getItem().getType() == Material.EMERALD && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            event.getPlayer().teleport(lastDamageLocation);
        }
    }

}