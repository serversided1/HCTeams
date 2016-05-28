package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BorderListener implements Listener {

    public static int BORDER_SIZE = 3000;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Math.abs(event.getBlock().getX()) > BORDER_SIZE || Math.abs(event.getBlock().getZ()) > BORDER_SIZE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Math.abs(event.getBlock().getX()) > BORDER_SIZE || Math.abs(event.getBlock().getZ()) > BORDER_SIZE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (Math.abs(event.getTo().getBlockX()) > BORDER_SIZE || Math.abs(event.getTo().getBlockZ()) > BORDER_SIZE) {
            Location newLocation = event.getTo().clone();

            while (Math.abs(newLocation.getX()) > BORDER_SIZE) {
                newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
            }

            while (Math.abs(newLocation.getZ()) > BORDER_SIZE) {
                newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
            }

            event.setTo(newLocation);
            event.getPlayer().sendMessage(ChatColor.RED + "That portal's location is past the border. It has been moved inwards.");
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (!event.getTo().getWorld().equals(event.getFrom().getWorld())) {
            return;
        }

        if (event.getTo().distance(event.getFrom()) < 0 || event.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN) {
            return;
        }

        if (Math.abs(event.getTo().getBlockX()) > BORDER_SIZE || Math.abs(event.getTo().getBlockZ()) > BORDER_SIZE) {
            Location newLocation = event.getTo().clone();

            while (Math.abs(newLocation.getX()) > BORDER_SIZE) {
                newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
            }

            while (Math.abs(newLocation.getZ()) > BORDER_SIZE) {
                newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
            }

           while (newLocation.getBlock().getType() != Material.AIR) {
               newLocation.setY(newLocation.getBlockY() + 1);
           }

            event.setTo(newLocation);
            event.getPlayer().sendMessage(ChatColor.RED + "That location is past the border.");
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ() || from.getBlockY() != to.getBlockY()) {
            if (Math.abs(event.getTo().getBlockX()) > BORDER_SIZE || Math.abs(event.getTo().getBlockZ()) > BORDER_SIZE) {
                if (event.getPlayer().getVehicle() != null) {
                    event.getPlayer().getVehicle().eject();
                }

                Location newLocation = event.getTo().clone();
                int tries = 0;

                while (Math.abs(newLocation.getX()) > BORDER_SIZE && tries++ < 100) {
                    newLocation.setX(newLocation.getX() - (newLocation.getX() > 0 ? 1 : -1));
                }

                if (tries >= 99) {
                    Foxtrot.getInstance().getLogger().severe("The server would have crashed while doing border checks! New X: " + newLocation.getX() + ", Old X: " + event.getTo().getBlockX());
                    return;
                }

                tries = 0;

                while (Math.abs(newLocation.getZ()) > BORDER_SIZE && tries++ < 100) {
                    newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0 ? 1 : -1));
                }

                if (tries >= 99) {
                    Foxtrot.getInstance().getLogger().severe("The server would have crashed while doing border checks! New Z: " + newLocation.getZ() + ", Old Z: " + event.getTo().getBlockZ());
                    return;
                }

                while (newLocation.getBlock().getType() != Material.AIR) {
                    newLocation.setY(newLocation.getBlockY() + 1);
                }

                event.setTo(newLocation);
                event.getPlayer().sendMessage(ChatColor.RED + "You have hit the border!");
            }
        }
    }

}
