package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.game.games.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.game.games.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.game.games.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.game.games.koth.events.KOTHControlTickEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHActivated(KOTHActivatedEvent event) {
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "The " + ChatColor.YELLOW + event.getKoth().getName() + " KOTH" + ChatColor.GOLD + " is now able to be controlled.");
    }

    @EventHandler
    public void onKOTHCap(KOTHCapturedEvent event) {
        Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(event.getPlayer().getName());
        String teamName = ChatColor.RED + "[-]";

        if (team != null) {
            teamName = ChatColor.RED + "[" + team.getFriendlyName() + ChatColor.RED + "]";
        }

        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "The " + ChatColor.YELLOW + event.getKoth().getName() + " KOTH" + ChatColor.GOLD + " has been controlled by " + ChatColor.RED + teamName + " " + event.getPlayer().getDisplayName() + ChatColor.GOLD + ".");
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "Reward: " + ChatColor.YELLOW + "One key for... something.");
    }

    @EventHandler
    public void onKOTHControlList(KOTHControlLostEvent event) {
        if (event.getKoth().getRemainingCapTime() <= (event.getKoth().getCapTime() - 30)) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] Control of " + ChatColor.YELLOW + event.getKoth().getName() + ChatColor.GOLD + " lost.");
        }
    }

    @EventHandler
    public void onKOTHControlTick(KOTHControlTickEvent event) {
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + event.getKoth().getName() + ChatColor.GOLD + " is trying to be controlled.");
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + " - Time left: " + ChatColor.BLUE + TimeUtils.getMMSS(event.getKoth().getRemainingCapTime()));
    }

}