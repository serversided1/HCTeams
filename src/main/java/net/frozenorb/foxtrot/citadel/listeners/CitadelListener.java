package net.frozenorb.foxtrot.citadel.listeners;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.citadel.events.CitadelActivatedEvent;
import net.frozenorb.foxtrot.citadel.events.CitadelCapturedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.event.HourEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;

public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivated(KOTHActivatedEvent event) {
        if (event.getKOTH().getName().equalsIgnoreCase("Citadel")) {
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(new CitadelActivatedEvent());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onKOTHCaptured(KOTHCapturedEvent event) {
        if (event.getKOTH().getName().equalsIgnoreCase("Citadel")) {
            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

            if (playerTeam != null) {
                Foxtrot.getInstance().getCitadelHandler().setCapper(playerTeam.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onCitadelActivated(CitadelActivatedEvent event) {
        Foxtrot.getInstance().getCitadelHandler().setCapper(null);
    }

    @EventHandler
    public void onCitadelCaptured(CitadelCapturedEvent event) {
        Foxtrot.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "Citadel" + ChatColor.YELLOW + " is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(Foxtrot.getInstance().getCitadelHandler().getLootable()) + ChatColor.YELLOW + ".");
    }

    @EventHandler(priority=EventPriority.MONITOR) // The monitor is here so we get called 'after' most join events.
    public void onPlayerJoin(PlayerJoinEvent event) {
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
        Object capper = Foxtrot.getInstance().getCitadelHandler().getCapper();

        if (playerTeam != null && capper == playerTeam.getUniqueId()) {
            event.getPlayer().sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_GREEN + "Your team currently controls Citadel.");
        }
    }


    @EventHandler
    public void onHour(HourEvent event) {
        // Every other hour
        if (event.getHour() % 2 == 0) {
            int respawned = Foxtrot.getInstance().getCitadelHandler().respawnCitadelChests();

            if (respawned != 0) {
                Foxtrot.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Citadel loot chests have respawned!");
            }
        }
    }

}