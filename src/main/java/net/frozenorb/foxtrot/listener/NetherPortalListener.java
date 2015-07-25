package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class NetherPortalListener implements Listener {

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event){
        Player player = event.getPlayer();

        if (event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            return;
        }

        if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom())){
                event.setCancelled(true);

                player.teleport(Foxtrot.getInstance().getServer().getWorld("world").getSpawnLocation());
                player.sendMessage(ChatColor.GREEN + "Teleported to overworld spawn!");
            }
        }
    }

}