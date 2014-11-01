package net.frozenorb.foxtrot.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

public class EndListener implements Listener {

	/*@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (e.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
			if (((Damageable) e.getPlayer()).getHealth() > 0D) {
				e.getPlayer().setHealth(0D);
			}
		}
	}*/

    HashMap<String, Long> msgCooldown = new HashMap<>();

    @EventHandler
    public void onPortal(PlayerPortalEvent event){
        Player player = event.getPlayer();

        if(event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL){
            event.setCancelled(true);

            if(!(msgCooldown.containsKey(player.getName())) || msgCooldown.get(player.getName()) < System.currentTimeMillis()){
                event.getPlayer().sendMessage(ChatColor.RED + "End Portals have been disabled for the first stage of Alpha! Check back [very] soon!");
                msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
            }
        }
    }
}
