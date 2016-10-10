package net.frozenorb.foxtrot.listener;

import net.frozenorb.basic.commands.event.PlayerRequestReportEvent;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.TimeUnit;

public class TeamRequestSpamListener implements Listener {

    @EventHandler
    public void onPlayerReportRequest(PlayerRequestReportEvent event) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

        if (team == null) {
            return;
        }

        long lastTeamRequestMsAgo = System.currentTimeMillis() - team.getLastRequestReport();

        if (lastTeamRequestMsAgo < TimeUnit.MINUTES.toMillis(1)) {
            event.setCancelled(true);
            event.setCancelledMessage(ChatColor.RED + "Someone on your team has recently made a request/report.");
        } else {
            team.setLastRequestReport(System.currentTimeMillis());
        }
    }

}