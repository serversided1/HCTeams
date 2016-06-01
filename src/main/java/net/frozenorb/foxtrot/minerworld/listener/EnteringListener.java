package net.frozenorb.foxtrot.minerworld.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EnteringListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        int radius = Foxtrot.getInstance().getMinerWorldHandler().getPortalRadius();
        Location portalLocation = Foxtrot.getInstance().getMinerWorldHandler().getPortalLocation();

        if (player.getWorld().equals(portalLocation.getWorld())) {
            if (player.getLocation().distance(portalLocation) <= radius) {
                if (!Foxtrot.getInstance().getMinerWorldHandler().canEnter(player)) {
                    player.sendMessage(ChatColor.RED + "You can't enter the Miner World at the moment. There are already " + Foxtrot.getInstance().getMinerWorldHandler().getMaxFactionAmount() + " of your faction members there.");
                    event.setCancelled(true);
                    player.teleport(player.getWorld().getSpawnLocation());
                } else {
                    Foxtrot.getInstance().getMinerWorldHandler().enter(player);
                    player.sendMessage(ChatColor.YELLOW + "You have entered the " + ChatColor.GREEN + "Miner World" + ChatColor.YELLOW + ".");
                }
            }
        }
    }

}
