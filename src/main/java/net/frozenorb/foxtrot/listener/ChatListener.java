package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.commands.team.TeamMuteCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamShadowMuteCommand;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.chat.ChatMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class ChatListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
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

        switch (chatMode) {
            case PUBLIC:
                if (event.isCancelled()) {
                    return;
                }

                if (TeamMuteCommand.factionMutes.containsKey(event.getPlayer().getName())) {
                    event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your faction is muted!");
                    event.setCancelled(true);

                    for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                        if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(player.getName()).contains(team.getName().toLowerCase())) {
                            player.sendMessage(ChatColor.GOLD + "[" + ChatColor.DARK_PURPLE + "FM: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.GRAY + event.getPlayer().getName() + ": " + event.getMessage());
                        }
                    }

                    return;
                }

                if (team == null) {
                    event.setFormat(ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]" + highRollerString + ChatColor.WHITE + "%s" + ChatColor.WHITE + ": %s");
                    String finalMessage = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

                    for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                        if (TeamShadowMuteCommand.factionShadowMutes.containsKey(event.getPlayer().getName())) {
                            continue;
                        }

                        if (event.getPlayer().isOp() || FoxtrotPlugin.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getName())) {
                            player.sendMessage(finalMessage);
                        }
                    }

                    event.setCancelled(true);
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
                            if (TeamShadowMuteCommand.factionShadowMutes.containsKey(event.getPlayer().getName())) {
                                if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(player.getName()).contains(team.getName().toLowerCase())) {
                                    player.sendMessage(ChatColor.GOLD + "[" + ChatColor.LIGHT_PURPLE + "SM: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.GRAY + event.getPlayer().getName() + ": " + event.getMessage());
                                }

                                continue;
                            }

                            if (event.getPlayer().isOp() || FoxtrotPlugin.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getName())) {
                                player.sendMessage(finalMessage);
                            }
                        }
                    }

                    event.setCancelled(true);
                    FoxtrotPlugin.getInstance().getServer().getConsoleSender().sendMessage(finalMessage);
                }

                break;
            case ALLIANCE:
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(player) || team.isAlly(player)) {
                        player.sendMessage(ChatColor.LIGHT_PURPLE + "(Ally) " + event.getPlayer().getName() + ": " + ChatColor.YELLOW + event.getMessage());
                    }
                }

                //TODO: Log this properly
                FactionActionTracker.logAction(team, "allychat", event.getPlayer().getName() + ": " + event.getMessage());
                FoxtrotPlugin.getInstance().getServer().getLogger().info("[Ally Chat] [" + team.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                event.setCancelled(true);

                break;
            case TEAM:
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(player)) {
                        player.sendMessage(ChatColor.DARK_AQUA + "(Team) " + event.getPlayer().getName() + ": " + ChatColor.YELLOW + event.getMessage());
                    } else if (FoxtrotPlugin.getInstance().getChatSpyMap().getChatSpy(player.getName()).contains(team.getName().toLowerCase())) {
                        player.sendMessage(ChatColor.GOLD + "[" + ChatColor.RED + "FC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.GRAY + event.getPlayer().getName() + ": " + event.getMessage());
                    }
                }

                FactionActionTracker.logAction(team, "teamchat", event.getPlayer().getName() + ": " + event.getMessage());
                FoxtrotPlugin.getInstance().getServer().getLogger().info("[Team Chat] [" + team.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                event.setCancelled(true);

                break;
        }
    }

}