package net.frozenorb.foxtrot.server;

import org.bukkit.event.player.PlayerMoveEvent;

public interface RegionMoveHandler {

	public boolean handleMove(PlayerMoveEvent event);

}