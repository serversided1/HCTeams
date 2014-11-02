package net.frozenorb.foxtrot.listener;

import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by chasechocolate.
 */
public class RoadListener implements Listener {
    private boolean isRoad(Location loc){
        return RegionManager.get().hasTag(loc, "road");
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event){
        if(event.isSticky()){
            Block block = event.getRetractLocation().getBlock();
            Location loc = block.getLocation();

            if(isRoad(loc)){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event){
        Block block = event.getBlock();
        Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
        Location loc = targetBlock.getLocation();

        if(isRoad(loc)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event){
        if(isRoad(event.getBlock().getLocation())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if(FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(player)){
            return;
        }

        if(isRoad(event.getBlock().getLocation())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();

        if(FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(player)){
            return;
        }

        if(isRoad(event.getBlock().getLocation())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event){
        if(isRoad(event.getBlock().getLocation())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack hand = player.getItemInHand();

        if(FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(player)){
            return;
        }

        if(event.getClickedBlock() != null && isRoad(event.getClickedBlock().getRelative(event.getBlockFace()).getLocation())){
            if(hand.getType() == Material.FLINT_AND_STEEL || hand.getType() == Material.LAVA_BUCKET || (hand.getType() == Material.INK_SACK && hand.getData().getData() == (byte) 15)){
                event.getPlayer().sendMessage(ChatColor.DARK_AQUA + "Cancelling that interact event as you're on the road!");
                event.setCancelled(true);
            }
        }
    }
}