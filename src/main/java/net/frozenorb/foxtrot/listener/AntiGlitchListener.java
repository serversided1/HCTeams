package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.EnderPearl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiGlitchListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
    public void onVerticalBlockPlaceGlitch(BlockPlaceEvent event) {
        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null && event.isCancelled()) {
            event.getPlayer().teleport(event.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.getBlock().getType().name().contains("RAIL")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (block.getType().name().contains("RAIL")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            new BukkitRunnable() {

                public void run() {
                    if (!event.getEntity().isValid()) {
                        cancel();
                        return;
                    }

                    int chunkX = event.getEntity().getLocation().getBlockX() >> 4;
                    int chunkZ = event.getEntity().getLocation().getBlockZ() >> 4;

                    //TODO: Make this async...
                    ((CraftWorld) event.getEntity().getWorld()).getChunkAt(chunkX + 1, chunkZ);
                    ((CraftWorld) event.getEntity().getWorld()).getChunkAt(chunkX - 1, chunkZ);
                    ((CraftWorld) event.getEntity().getWorld()).getChunkAt(chunkX, chunkZ + 1);
                    ((CraftWorld) event.getEntity().getWorld()).getChunkAt(chunkX, chunkZ - 1);
                }

            }.runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
        }
    }

}