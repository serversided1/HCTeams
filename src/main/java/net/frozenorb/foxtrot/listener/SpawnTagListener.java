package net.frozenorb.foxtrot.listener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.util.PlayerUtils;

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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || !SpawnTagHandler.isTagged(player) || Foxtrot.getInstance().getServerHandler().isVeltKitMap() || Foxtrot.getInstance().getServerHandler().isPlaceBlocksInCombat()) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't place blocks whilst in combat.");
        event.setCancelled(true);
    }

}