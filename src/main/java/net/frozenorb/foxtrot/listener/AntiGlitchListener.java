package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
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

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getExited();
        Location location = player.getLocation();

        while (location.getBlock().getType().isSolid()) {
            location.add(0, 1, 0);
        }

        while (location.getBlock().getType().isSolid()) {
            location.subtract(0, 1, 0);
        }

        final Location locationFinal = location;

        new BukkitRunnable() {

            public void run () {
                player.teleport(locationFinal);
            }

        }.runTaskLater(Foxtrot.getInstance(), 1L);
    }

}