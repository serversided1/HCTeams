package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class PortalTrapListener implements Listener {

    @EventHandler
    public void onPortal(PlayerPortalEvent event){
        Player player = event.getPlayer();

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }

        if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getFrom())){
                event.setCancelled(true);

                player.teleport(FoxtrotPlugin.getInstance().getServer().getWorld("world").getSpawnLocation());
                player.sendMessage(ChatColor.GREEN + "Teleported to overworld spawn!");
                return;
            }

            /*new BukkitRunnable() {

                public void run() {
                    Block portalBlock = event.getPlayer().getLocation().getBlock();
                    boolean northSouth;

                    if (portalBlock.getRelative(BlockFace.NORTH).getType() == Material.PORTAL || portalBlock.getRelative(BlockFace.NORTH).getType() == Material.PORTAL) {
                        northSouth = true;
                    } else if (portalBlock.getRelative(BlockFace.WEST).getType() == Material.PORTAL || portalBlock.getRelative(BlockFace.EAST).getType() == Material.PORTAL) {
                        northSouth = false;
                    } else {
                        return;
                    }

                    if (!northSouth) {
                        for (int y = 0; y < 3; y++) {
                            portalBlock.getRelative(BlockFace.NORTH).getLocation().clone().add(0, y, 0).getBlock().setType(Material.AIR);
                            portalBlock.getRelative(BlockFace.NORTH).getLocation().clone().add(-1, y, 0).getBlock().setType(Material.AIR);
                            portalBlock.getRelative(BlockFace.SOUTH).getLocation().clone().add(0, y, 0).getBlock().setType(Material.AIR);
                            portalBlock.getRelative(BlockFace.SOUTH).getLocation().clone().add(-1, y, 0).getBlock().setType(Material.AIR);
                        }
                    } else {
                        for (int y = 0; y < 3; y++) {
                            portalBlock.getRelative(BlockFace.WEST).getLocation().clone().add(0, y, 0).getBlock().setType(Material.AIR);
                            portalBlock.getRelative(BlockFace.WEST).getLocation().clone().add(0, y, -1).getBlock().setType(Material.AIR);
                            portalBlock.getRelative(BlockFace.EAST).getLocation().clone().add(0, y, 0).getBlock().setType(Material.AIR);
                            portalBlock.getRelative(BlockFace.EAST).getLocation().clone().add(0, y, -1).getBlock().setType(Material.AIR);
                        }
                    }

                    Bukkit.broadcastMessage(event.getPlayer().getName() + ChatColor.DARK_AQUA + " That portal is facing north/south? " + northSouth);
                }

            }.runTaskLater(FoxtrotPlugin.getInstance(), 1L);*/
        } else if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
            /*new BukkitRunnable() {

                public void run() {
                    Block portalBlock = event.getPlayer().getLocation().getBlock();
                    boolean northSouth;

                    if (portalBlock.getRelative(BlockFace.NORTH).getType() == Material.PORTAL || portalBlock.getRelative(BlockFace.NORTH).getType() == Material.PORTAL) {
                        northSouth = true;
                    } else if (portalBlock.getRelative(BlockFace.WEST).getType() == Material.PORTAL || portalBlock.getRelative(BlockFace.EAST).getType() == Material.PORTAL) {
                        northSouth = false;
                    } else {
                        return;
                    }

                    portalBlock = portalBlock.getRelative(BlockFace.DOWN);

                    if (northSouth) {
                        for (int x = 1; x > -3; x--) {
                            for (int z = -4; z <= 4; z++) {
                                if (z == 0) {
                                    continue;
                                }

                                for (int y = 0; y < 4; y++) {
                                    portalBlock.getLocation().clone().add(x, y, z).getBlock().setType(y == 0 ? Material.OBSIDIAN : Material.AIR);
                                }
                            }
                        }
                    } else {
                        for (int x = 1; x > -3; x--) {
                            for (int z = -4; z <= 4; z++) {
                                if (z == 0) {
                                    continue;
                                }

                                for (int y = 0; y < 5; y++) {
                                    portalBlock.getLocation().clone().add(z, y, x).getBlock().setType(y == 0 ? Material.OBSIDIAN : Material.AIR);
                                }
                            }
                        }
                    }

                    Bukkit.broadcastMessage(event.getPlayer().getName() + ChatColor.DARK_AQUA + " That portal is facing north/south? " + northSouth);
                }

            }.runTaskLater(FoxtrotPlugin.getInstance(), 1L);*/
        }
    }

}