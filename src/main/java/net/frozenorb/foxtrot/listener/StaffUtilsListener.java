package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.nametag.NametagManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by macguy8 on 11/12/2014.
 */
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

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        ItemStack oldSlot = event.getPlayer().getInventory().getItem(event.getPreviousSlot());

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE && oldSlot != null && oldSlot.getType() == Material.REDSTONE_BLOCK) {
            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                new BukkitRunnable() {

                    public void run() {
                        NametagManager.reloadPlayer(player, event.getPlayer());
                    }

                }.runTaskLater(FoxtrotPlugin.getInstance(), 2L);
            }
        }
    }

}