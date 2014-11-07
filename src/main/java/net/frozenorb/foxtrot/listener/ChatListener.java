package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Mute;
import net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.ShadowMute;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Set;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class ChatListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        String highRollerString = FoxtrotPlugin.getInstance().getServerHandler().getHighRollers().contains(event.getPlayer().getName()) ? ChatColor.DARK_PURPLE + "[HighRoller]" : "";

        if (team == null) {
            if (Mute.factionMutes.containsKey(event.getPlayer().getName())) {
                event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction is muted!");
                event.setCancelled(true);
                return;
            }

            event.setFormat(ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]" + highRollerString + ChatColor.WHITE + "%s" + ChatColor.WHITE + ": %s");
            return;
        }

        event.setFormat(ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getFriendlyName() + ChatColor.GOLD + "]" + highRollerString + ChatColor.WHITE + "%s" + ChatColor.WHITE + ": %s");

        Set<String> members = team.getMembers();
        boolean doTeamChat = event.getMessage().startsWith("@");
        boolean doGlobalChat = event.getMessage().startsWith("!");

        if (doTeamChat || doGlobalChat) {
            event.setMessage(event.getMessage().substring(1));
        }

        event.setCancelled(true);

        if (!doGlobalChat && (event.getPlayer().hasMetadata("teamChat") || doTeamChat)) {
            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                if (members.contains(player.getName())) {
                    player.sendMessage(ChatColor.DARK_AQUA + "(Team) " + event.getPlayer().getName() + ": " + ChatColor.YELLOW + event.getMessage());
                }
            }

            FactionActionTracker.logAction(team, "chat", event.getPlayer().getName() + ": " + event.getMessage());
            FoxtrotPlugin.getInstance().getServer().getLogger().info("[TeamChat] [" + team.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
            return;
        }

        if (Mute.factionMutes.containsKey(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction is muted!");
            return;
        }

        String finalMessage = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player)) {
                player.sendMessage(finalMessage.replace("§6[§e", "§6[§2"));
            } else {
                if (!ShadowMute.factionShadowMutes.containsKey(event.getPlayer().getName())) {
                    player.sendMessage(finalMessage);
                }
            }
        }

        FoxtrotPlugin.getInstance().getServer().getConsoleSender().sendMessage(finalMessage);
    }

}