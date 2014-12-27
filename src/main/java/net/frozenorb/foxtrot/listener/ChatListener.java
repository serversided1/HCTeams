package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.chat.ChatMode;
import net.frozenorb.foxtrot.team.commands.team.TeamMuteCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamShadowMuteCommand;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class ChatListener implements Listener {

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());
        String highRollerString = FoxtrotPlugin.getInstance().getServerHandler().getHighRollers().contains(event.getPlayer().getName()) ? ChatColor.DARK_PURPLE + "[HighRoller]" : "";
        ChatMode chatMode = FoxtrotPlugin.getInstance().getChatModeMap().getChatMode(event.getPlayer().getName());

        boolean doTeamChat = event.getMessage().startsWith("@");
        boolean doAllyChat = event.getMessage().startsWith("#");
        boolean doGlobalChat = event.getMessage().startsWith("!");

        if (doTeamChat || doGlobalChat || doAllyChat) {
            event.setMessage(event.getMessage().substring(1));
        }

        if (doGlobalChat || team == null) {
            chatMode = ChatMode.PUBLIC;
        } else if (doTeamChat) {
            chatMode = ChatMode.TEAM;
        } else if (doAllyChat) {
            chatMode = ChatMode.ALLIANCE;
        }

        if (event.isCancelled()) {
            return;
        }

        // Any route we go down will cancel the event eventually.
        // Let's just save space and do it here.
        event.setCancelled(true);

        switch (chatMode) {
            case PUBLIC:
                if (TeamMuteCommand.getTeamMutes().containsKey(event.getPlayer().getName())) {
                    event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team is muted!");

                    // Notify those with chatspy on the team.
                    for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                        if (team != null && FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(player.getName()).contains(team.getUniqueId())) {
                            player.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_PURPLE + "M: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.DARK_PURPLE + event.getPlayer().getName() + ": " + event.getMessage());
                        }
                    }

                    return;
                }

                // TODO: Rewrite
                if (team == null) {
                    event.setFormat(ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]" + highRollerString + ChatColor.WHITE + "%s" + ChatColor.WHITE + ": %s");
                    String finalMessage = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

                    for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                        if (TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getName())) {
                            continue;
                        }

                        if (event.getPlayer().isOp() || FoxtrotPlugin.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getName())) {
                            player.sendMessage(finalMessage);
                        }
                    }

                    FoxtrotPlugin.getInstance().getServer().getConsoleSender().sendMessage(finalMessage);
                } else {
                    event.setFormat(ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + highRollerString + ChatColor.WHITE + "%s" + ChatColor.WHITE + ": %s");
                    String finalMessage = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

                    for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                        if (team.isMember(player)) {
                            player.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + ChatColor.YELLOW, ChatColor.GOLD + "[" + ChatColor.DARK_GREEN));
                        } else if (team.isAlly(player)) {
                            player.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + ChatColor.YELLOW, ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE));
                        } else {
                            if (TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getName())) {
                                if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(player.getName()).contains(team.getUniqueId())) {
                                    player.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_PURPLE + "SM: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.DARK_PURPLE + event.getPlayer().getName() + ": " + event.getMessage());
                                }

                                continue;
                            }

                            if (event.getPlayer().isOp() || FoxtrotPlugin.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getName())) {
                                player.sendMessage(finalMessage);
                            }
                        }
                    }

                    FoxtrotPlugin.getInstance().getServer().getConsoleSender().sendMessage(finalMessage);
                }

                break;
            case ALLIANCE:
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(player) || team.isAlly(player)) { // If they're going to receive this message, send it!
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "(Ally) " + event.getPlayer().getName() + ": " + ChatColor.YELLOW + event.getMessage());
                    } else if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(player.getName()).contains(team.getUniqueId())) {
                        // Chat spying for allies are weird.
                        // You'll only see the message if you're spying on the team SENDING it.
                        // It might be best to make it so spying on ONE team lets you see the chat of the alliance,
                        // but then we get teams where A is allied to B, B is allied to C, and A isn't allied to C.
                        // @itsjhalt
                        player.sendMessage(ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "AC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + ": " + event.getMessage());
                    }
                }

                // Log to ally's allychat log.
                for (ObjectId allyId : team.getAllies()) {
                    Team ally = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(allyId);

                    if (ally != null) {
                        FactionActionTracker.logAction(ally, "allychat", "[" + team.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                    }
                }

                // Log to our own allychat log.
                FactionActionTracker.logAction(team, "allychat", "[" + team.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                FoxtrotPlugin.getInstance().getServer().getLogger().info("[Ally Chat] [" + team.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
            case TEAM:
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(player)) { // If they're going to receive this message, send it!
                        player.sendMessage(ChatColor.DARK_AQUA + "(Team) " + event.getPlayer().getName() + ": " + ChatColor.YELLOW + event.getMessage());
                    } else if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(player.getName()).contains(team.getUniqueId())) { // Otherwise, if they're going to chat spy it...
                        player.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_AQUA + "TC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.DARK_AQUA + event.getPlayer().getName() + ": " + event.getMessage());
                    }
                }

                // Log to our teamchat log.
                FactionActionTracker.logAction(team, "teamchat", event.getPlayer().getName() + ": " + event.getMessage());
                FoxtrotPlugin.getInstance().getServer().getLogger().info("[Team Chat] [" + team.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
        }
    }

}