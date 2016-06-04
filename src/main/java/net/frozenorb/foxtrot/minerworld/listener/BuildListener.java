package net.frozenorb.foxtrot.minerworld.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.minerworld.blockregen.BlockRegenHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().equals(Foxtrot.getInstance().getMinerWorldHandler().getWorld())) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Block block = event.getBlock();

        if (BlockRegenHandler.shouldRegen(block.getType()) && !DTRBitmask.SAFE_ZONE.appliesAt(block.getLocation())) {
            event.setCancelled(false); // we do this because it might be cancelled somewhere else
            Foxtrot.getInstance().getMinerWorldHandler().getBlockRegenHandler().regen(block);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().equals(Foxtrot.getInstance().getMinerWorldHandler().getWorld())) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
    }

}
