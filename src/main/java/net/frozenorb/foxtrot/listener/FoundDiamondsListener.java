package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class FoundDiamondsListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.DIAMOND_ORE) {
            event.getBlock().setMetadata("DiamondPlaced", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.DIAMOND_ORE && !event.getBlock().hasMetadata("DiamondPlaced")) {
            int diamonds = 0;

            for (int x = -5; x < 5; x++) {
                for (int y = -5; y < 5; y++) {
                    for (int z = -5; z < 5; z++) {
                        Block block = event.getBlock().getLocation().add(x, y, z).getBlock();

                        if (block.getType() == Material.DIAMOND_ORE && !block.hasMetadata("DiamondPlaced")) {
                            diamonds++;
                            block.setMetadata("DiamondPlaced", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                        }
                    }
                }
            }

            FoxtrotPlugin.getInstance().getServer().broadcastMessage("[FD] " + ChatColor.AQUA + event.getPlayer().getName() + " found " + diamonds + " diamond" + (diamonds == 1 ? "" : "s") + ".");
        }
    }

}