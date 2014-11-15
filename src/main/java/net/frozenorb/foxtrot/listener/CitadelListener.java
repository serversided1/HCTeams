package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by macguy8 on 11/15/2014.
 */
public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivate(KOTHActivatedEvent event) {
        if (!event.getKoth().getName().equalsIgnoreCase("Citadel")) {
            return;
        }

        FoxtrotPlugin.getInstance().getCitadelHandler().setCapper("none");
    }

    @EventHandler
    public void onKOTHCaptured(KOTHCapturedEvent event) {
        if (!event.getKoth().getName().equalsIgnoreCase("Citadel")) {
            return;
        }

        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

        if (playerTeam != null) {
            FoxtrotPlugin.getInstance().getCitadelHandler().setCapper(playerTeam.getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        String citadelCappers = FoxtrotPlugin.getInstance().getCitadelHandler().getCapper();

        if (playerTeam != null && citadelCappers != null && citadelCappers.equalsIgnoreCase(playerTeam.getName())) {
            // Send the message on a delay so other login info (IE the /f who every player runs) doesn't block it out.
            new BukkitRunnable() {

                public void run() {
                    event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Your faction currently controls Citadel.");
                }

            }.runTaskLater(FoxtrotPlugin.getInstance(), 1L);
        }
    }

}