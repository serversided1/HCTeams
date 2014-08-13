package net.frozenorb.foxtrot.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class BorderListener implements Listener {

	public static final int BORDER_SIZE = 2500;

	@EventHandler
	public void onBlockBreak(final BlockPlaceEvent e) {
		Location locs = e.getBlock().getLocation();
		if (Math.abs(locs.getBlockX()) > BORDER_SIZE || Math.abs(locs.getBlockZ()) > BORDER_SIZE) {

			e.setCancelled(true);
			e.setBuild(false);
			return;
		}

	}

	@EventHandler
	public void onPortal(PlayerPortalEvent e) {
		Location locs = e.getTo();
		if (Math.abs(locs.getBlockX()) > BORDER_SIZE || Math.abs(locs.getBlockZ()) > BORDER_SIZE) {
			e.getPlayer().sendMessage(ChatColor.RED + "That portal's location is past the border.");
			if (locs.getX() > BORDER_SIZE)
				locs.setX(BORDER_SIZE - 2);
			if (locs.getZ() > BORDER_SIZE)
				locs.setZ(BORDER_SIZE - 2);
			e.setCancelled(true);

		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (!e.getTo().getChunk().isLoaded()) {
			e.getTo().getChunk().load();
		}

		if (e.getTo().getWorld().getName().equals(e.getFrom().getWorld().getName())) {
			if (e.getTo().distance(e.getFrom()) > 0) {
				Location locs = e.getTo();
				if (Math.abs(locs.getBlockX()) > BORDER_SIZE || Math.abs(locs.getBlockZ()) > BORDER_SIZE) {
					e.getPlayer().sendMessage(ChatColor.RED + "That location is past the border.");
					if (locs.getX() > BORDER_SIZE)
						locs.setX(BORDER_SIZE - 2);
					if (locs.getZ() > BORDER_SIZE)
						locs.setZ(BORDER_SIZE - 2);
					e.setTo(e.getFrom());
					e.getPlayer().setVelocity(new Vector(0, 0, 0));

				}
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Location from = e.getFrom();
		double fromY = from.getY();
		Location to = e.getTo();
		double toY = to.getY();
		double fromX = from.getX();
		double fromZ = from.getZ();
		double toX = to.getX();
		double toZ = to.getZ();
		if (fromX != toX || fromZ != toZ || fromY != toY) {
			Location locs = e.getTo();
			if (Math.abs(locs.getBlockX()) > BORDER_SIZE || Math.abs(locs.getBlockZ()) > BORDER_SIZE) {
				if (e.getPlayer().getVehicle() != null)
					e.getPlayer().getVehicle().eject();
				e.getPlayer().sendMessage(ChatColor.RED + "You have hit the border!");
				Location loc = e.getFrom();
				loc.setPitch(e.getPlayer().getLocation().getPitch());
				loc.setYaw(e.getPlayer().getLocation().getYaw());
				e.getPlayer().teleport(loc);
				Vector vec = e.getFrom().toVector().subtract(e.getTo().toVector()).normalize();
				e.getPlayer().setVelocity(vec.multiply(0.8));

			}
		}
	}
}
