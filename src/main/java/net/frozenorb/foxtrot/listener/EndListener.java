package net.frozenorb.foxtrot.listener;

import org.bukkit.World.Environment;
import org.bukkit.entity.Damageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EndListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (e.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
			if (((Damageable) e.getPlayer()).getHealth() > 0D) {
				e.getPlayer().setHealth(0D);
			}
		}
	}
}
