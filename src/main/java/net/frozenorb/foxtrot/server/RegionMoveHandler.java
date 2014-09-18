package net.frozenorb.foxtrot.server;

import org.bukkit.event.player.PlayerMoveEvent;

public interface RegionMoveHandler {

	/**
	 * Handles the event in the region that it occured in.
	 * 
	 * @param e
	 *            the {@link PlayerMoveEvent} that the player triggered
	 * @return true if the event should continue, or false if the event handling
	 *         should stop
	 */
	public boolean handleMove(PlayerMoveEvent e);
}
