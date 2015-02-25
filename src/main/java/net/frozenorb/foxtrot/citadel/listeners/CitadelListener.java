package net.frozenorb.foxtrot.citadel.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.citadel.events.CitadelActivatedEvent;
import net.frozenorb.foxtrot.citadel.events.CitadelCapturedEvent;
import net.frozenorb.foxtrot.events.HourEvent;
import net.frozenorb.foxtrot.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.SimpleDateFormat;

public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivate(KOTHActivatedEvent event) {
        if (event.getKOTH().getName().equalsIgnoreCase("Citadel")) {
            FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new CitadelActivatedEvent());
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onKOTHCaptured(KOTHCapturedEvent event) {
        if (event.getKOTH().getName().equalsIgnoreCase("Citadel")) {
            Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

            if (playerTeam != null) {
                FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(playerTeam.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onCitadelActivated(CitadelActivatedEvent event) {
        FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(null);
    }

    @EventHandler
    public void onCitadelCaptured(CitadelCapturedEvent event) {
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.RED + "Citadel" + ChatColor.YELLOW + " is " + ChatColor.DARK_RED + "closed " + ChatColor.YELLOW + "until " + ChatColor.WHITE + (new SimpleDateFormat()).format(FoxtrotPlugin.getInstance().getCitadelHandler().getLootable()) + ChatColor.YELLOW + ".");
    }

    @EventHandler(priority=EventPriority.MONITOR) // The monitor is here so we get called 'after' most join events.
    public void onPlayerJoin(PlayerJoinEvent event) {
        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        Object capper = FoxtrotPlugin.getInstance().getCitadelHandler().getCapper();

        if (playerTeam != null && capper == playerTeam.getUniqueId()) {
            event.getPlayer().sendMessage(CitadelHandler.PREFIX + " " + ChatColor.DARK_GREEN + "Your team currently controls Citadel.");
        }
    }


    @EventHandler
    public void onHour(HourEvent event) {
        // Every other hour
        if (event.getHour() % 2 == 0) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.GREEN + "Citadel loot chests have respawned!");
            FoxtrotPlugin.getInstance().getCitadelHandler().respawnCitadelChests();
        }
    }

}