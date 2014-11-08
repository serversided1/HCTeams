package net.frozenorb.foxtrot.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class BorderListener implements Listener {

    // NEXT MAP
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

			if (event.getTo().getX() > BORDER_SIZE) {
                newLocation.setX(BORDER_SIZE - 2);
            }

			if (event.getTo().getZ() > BORDER_SIZE) {
                newLocation.setZ(BORDER_SIZE - 2);
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

        if (event.getTo().distance(event.getFrom()) < 0) {
            return;
        }

        if (Math.abs(event.getTo().getBlockX()) > BORDER_SIZE || Math.abs(event.getTo().getBlockZ()) > BORDER_SIZE) {
            Location newLocation = event.getTo().clone();

            if (event.getTo().getX() > BORDER_SIZE) {
                newLocation.setX(BORDER_SIZE - 2);
            }

            if (event.getTo().getZ() > BORDER_SIZE) {
                newLocation.setZ(BORDER_SIZE - 2);
            }

            event.setTo(newLocation);
            event.getPlayer().setVelocity(new Vector(0, 0, 0));
            event.getPlayer().sendMessage(ChatColor.RED + "That location is past the border.");
        }
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();

		if (from.getX() != to.getX() || from.getZ() != to.getZ() || from.getY() != to.getY()) {
			if (Math.abs(event.getTo().getBlockX()) > BORDER_SIZE || Math.abs(event.getTo().getBlockZ()) > BORDER_SIZE) {
				if (event.getPlayer().getVehicle() != null) {
                    event.getPlayer().getVehicle().eject();
                }

                Location newLocation = event.getTo().clone();

                if (event.getTo().getX() > BORDER_SIZE) {
                    newLocation.setX(BORDER_SIZE - 2);
                }

                if (event.getTo().getZ() > BORDER_SIZE) {
                    newLocation.setZ(BORDER_SIZE - 2);
                }

                event.getPlayer().teleport(newLocation);
                event.getPlayer().sendMessage(ChatColor.RED + "You have hit the border!");
			}
		}
	}

}