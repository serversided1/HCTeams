package net.frozenorb.foxtrot.minerworld.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.TimeUnit;

public class EnteringListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        int radius = Foxtrot.getInstance().getMinerWorldHandler().getPortalRadius();
        Location portalLocation = Foxtrot.getInstance().getMinerWorldHandler().getPortalLocation();

        if (portalLocation == null) {
            return;
        }

        if (player.getWorld().equals(portalLocation.getWorld())) {
            if (player.getLocation().distance(portalLocation) <= radius) {
                if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You can't enter the Miner World with PvP Protection.");
                    player.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                    player.teleport(player.getWorld().getSpawnLocation());
                    return;
                }

                if (Foxtrot.getInstance().getPlaytimeMap().getPlaytime(event.getPlayer().getUniqueId()) < TimeUnit.HOURS.toSeconds(1)) {
                    player.sendMessage(ChatColor.RED + "You must have at least one hour of playtime before you can enter the Miner World.");
                    player.teleport(player.getWorld().getSpawnLocation());
                    return;
                }

                if (!Foxtrot.getInstance().getMinerWorldHandler().canEnter(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You can't enter the Miner World at the moment. There are already " + Foxtrot.getInstance().getMinerWorldHandler().getMaxFactionAmount() + " of your faction members there.");
                    player.teleport(player.getWorld().getSpawnLocation());
                    return;
                } else {
                    Foxtrot.getInstance().getMinerWorldHandler().enter(player);
                    player.sendMessage(ChatColor.YELLOW + "You have entered the " + ChatColor.GREEN + "Miner World" + ChatColor.YELLOW + ".");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!player.getWorld().equals(Foxtrot.getInstance().getMinerWorldHandler().getWorld())) {
            return;
        }

        if (Foxtrot.getInstance().getMinerWorldHandler().isEnabled()) {
            if (!Foxtrot.getInstance().getMinerWorldHandler().canEnter(player.getUniqueId())) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "You're currently in the Miner World and there are already " + Foxtrot.getInstance().getMinerWorldHandler().getMaxFactionAmount() + " of your faction members there.\n" +
                        "You're not allowed to login.");
            }
        } else {
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

                player.sendMessage(ChatColor.RED + "Miner World is disabled. You have been teleported to spawn.");
            }, 5L);
        }


    }

}
