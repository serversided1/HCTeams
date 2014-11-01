package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.game.games.koth.events.KOTHActivatedEvent;
import net.frozenorb.foxtrot.game.games.koth.events.KOTHCapturedEvent;
import net.frozenorb.foxtrot.game.games.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.game.games.koth.events.KOTHControlTickEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHActivated(KOTHActivatedEvent event) {
        Bukkit.broadcastMessage(ChatColor.GRAY + "█████████");
        Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
        Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.GOLD + "[KingOfTheHill]");
        Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "███" + ChatColor.GRAY + "███" + ChatColor.YELLOW + event.getKoth().getName() + " KOTH");
        Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "██" + ChatColor.GOLD + "can be contested now.");
        Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
        Bukkit.broadcastMessage(ChatColor.GRAY + "█" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "███" + ChatColor.DARK_AQUA + "█" + ChatColor.GRAY + "█");
        Bukkit.broadcastMessage(ChatColor.GRAY + "█████████");
    }

    @EventHandler
    public void onKOTHCap(KOTHCapturedEvent event) {
        Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(event.getPlayer().getName());
        String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";

        if (team != null) {
            teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getFriendlyName() + ChatColor.GOLD + "]";
        }

        for (int i = 0; i < 6; i++) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage("");
        }

        FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "has been captured by " + teamName + ChatColor.WHITE + event.getPlayer().getDisplayName() + ChatColor.YELLOW + "!");
    }

    @EventHandler
    public void onKOTHControlList(KOTHControlLostEvent event) {
        if (event.getKoth().getRemainingCapTime() <= (event.getKoth().getCapTime() - 30)) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] Control of " + ChatColor.YELLOW + event.getKoth().getName() + ChatColor.GOLD + " lost.");
        }
    }

    @EventHandler
    public void onKOTHControlTick(KOTHControlTickEvent event) {
        if (event.getKoth().getRemainingCapTime() <= (event.getKoth().getCapTime() - 30)) {
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + event.getKoth().getName() + ChatColor.GOLD + " is trying to be controlled.");
            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.GOLD + " - Time left: " + ChatColor.BLUE + TimeUtils.getMMSS((int) event.getKoth().getRemainingCapTime()));
        }
    }

}