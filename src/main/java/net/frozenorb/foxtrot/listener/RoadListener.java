package net.frozenorb.foxtrot.listener;

import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RoadListener implements Listener {

    private boolean isRoad(Location loc){
        return (RegionManager.get().hasTag(loc, "road"));
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event){
        if (event.isSticky() && isRoad(event.getRetractLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event){
        if (isRoad(event.getBlock().getRelative(event.getDirection(), event.getLength() + 1).getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event){
        if (isRoad(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (isRoad(event.getBlock().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on the road!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (isRoad(event.getBlock().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on the road!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event){
        if (isRoad(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (event.getClickedBlock() != null && isRoad(event.getClickedBlock().getRelative(event.getBlockFace()).getLocation())) {
            if (event.getPlayer().getItemInHand().getType() == Material.FLINT_AND_STEEL || event.getPlayer().getItemInHand().getType() == Material.LAVA_BUCKET || (event.getPlayer().getItemInHand().getType() == Material.INK_SACK && event.getPlayer().getItemInHand().getData().getData() == (byte) 15)) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on the road!");
                event.setCancelled(true);
            }
        }
    }

}