package net.frozenorb.foxtrot.chat.listeners;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.FoxConstants;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.ChatHandler;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamMuteCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamShadowMuteCommand;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
        String highRollerPrefix = Foxtrot.getInstance().getServerHandler().getHighRollers().contains(event.getPlayer().getUniqueId()) ? ChatHandler.HIGHROLLER_PREFIX : "";
        String customPrefix = Foxtrot.getInstance().getChatHandler().getCustomPrefix(event.getPlayer().getUniqueId());
        ChatMode playerChatMode = Foxtrot.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
        ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
        ChatMode finalChatMode;

        // If they provided us with a forced chat mode, we have to remove it from the final message.
        // We also .trim() the message because people will do things like '! hi', which just looks annoying in chat.
        if (forcedChatMode != null) {
            event.setMessage(event.getMessage().substring(1).trim());
        }

        if (forcedChatMode != null) {
            finalChatMode = forcedChatMode;
        } else {
            finalChatMode = playerChatMode;
        }

        // If another plugin cancelled this event before it got to us (we are on MONITOR, so it'll happen)
        if (event.isCancelled() && finalChatMode == ChatMode.PUBLIC) { // Only respect cancelled events if this is public chat. Who cares what their team says.
            return;
        }

        // Any route we go down will cancel the event eventually.
        // Let's just do it here.
        event.setCancelled(true);

        // If someone's not in a team, instead of forcing their 'channel' to public,
        // we just tell them they can't.
        if (finalChatMode != ChatMode.PUBLIC && playerTeam == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a team!");
            return;
        }

        // and here starts the big logic switch
        switch (finalChatMode) {
            case PUBLIC:
                if (TeamMuteCommand.getTeamMutes().containsKey(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team is muted!");
                    return;
                }

                // create simple public chat message
                FancyMessage simpleMessage = new FancyMessage("[").color(ChatColor.GOLD).then(playerTeam == null ? "-" : playerTeam.getName()).color(ChatColor.YELLOW);
                if (playerTeam != null) simpleMessage.command("/t i " + playerTeam.getName());
                simpleMessage.then("]").color(ChatColor.GOLD).then(highRollerPrefix).then(customPrefix).then(event.getPlayer().getDisplayName()).then(": " + event.getMessage());

                // create the message members see
                FancyMessage memberMessage = new FancyMessage("[").color(ChatColor.GOLD).then(playerTeam == null ? "-" : playerTeam.getName()).color(ChatColor.DARK_GREEN);
                if (playerTeam != null) memberMessage.command("/t i " + playerTeam.getName());
                memberMessage.then("]").color(ChatColor.GOLD).then(highRollerPrefix).then(customPrefix).then(event.getPlayer().getDisplayName()).then(": " + event.getMessage());

                // create the message allies see
                FancyMessage allyMessage = new FancyMessage("[").color(ChatColor.GOLD).then(playerTeam == null ? "-" : playerTeam.getName()).color(Team.ALLY_COLOR);
                if (playerTeam != null) allyMessage.command("/t i " + playerTeam.getName());
                allyMessage.then("]").color(ChatColor.GOLD).then(highRollerPrefix).then(customPrefix).then(event.getPlayer().getDisplayName()).then(": " + event.getMessage());

                // Loop those who are to receive the message (which they won't if they have the sender /ignore'd or something),
                // not online players
                for (Player player : event.getRecipients()) {
                    if (playerTeam == null) {
                        // If the player sending the message is shadowmuted (if their team was and they left it)
                        // then we don't allow them to. We probably could move this check "higher up", but oh well.
                        if (TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                            continue;
                        }

                        // If their chat is enabled (which it is by default) or the sender is op, send them the message
                        // The isOp() fragment is so OP messages are sent regardless of if the player's chat is toggled
                        if (event.getPlayer().isOp() || Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
                            simpleMessage.send(player);
                        }
                    } else {
                        // send the appropriate message
                        if (playerTeam.isMember(player.getUniqueId())) {
                            memberMessage.send(player);
                        } else if (playerTeam.isAlly(player.getUniqueId())) {
                            allyMessage.send(player);
                        } else {
                            // We only check this here as...
                            // Team members always see their team's messages
                            // Allies always see their allies' messages, 'cause they'll probably be in a TS or something
                            // and they could figure out this code even exists
                            if (TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                                continue;
                            }

                            // If their chat is enabled (which it is by default) or the sender is op, send them the message
                            // The isOp() fragment is so OP messages are sent regardless of if the player's chat is toggled
                            if (event.getPlayer().isOp() || Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
                                simpleMessage.send(player);
                            }
                        }
                    }
                }

                simpleMessage.send(Bukkit.getConsoleSender());
                break;
            case ALLIANCE:
                String allyChatFormat = FoxConstants.allyChatFormat(event.getPlayer(), event.getMessage());
                String allyChatSpyFormat = FoxConstants.allyChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                // Loop online players and not recipients just in case you're weird and
                // /ignore your teammates/allies
                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isMember(player.getUniqueId()) || playerTeam.isAlly(player.getUniqueId())) {
                        player.sendMessage(allyChatFormat);
                    } else if (Foxtrot.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
                        player.sendMessage(allyChatSpyFormat);
                    }
                }

                // Log to ally's allychat log.
                for (ObjectId allyId : playerTeam.getAllies()) {
                    Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);

                    if (ally != null) {
                        TeamActionTracker.logAction(ally, TeamActionType.ALLY_CHAT, "[" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                    }
                }

                // Log to our own allychat log.
                TeamActionTracker.logAction(playerTeam, TeamActionType.ALLY_CHAT, "[" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                Foxtrot.getInstance().getServer().getLogger().info("[Ally Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
            case TEAM:
                String teamChatFormat = FoxConstants.teamChatFormat(event.getPlayer(), event.getMessage());
                String teamChatSpyFormat = FoxConstants.teamChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                // Loop online players and not recipients just in case you're weird and
                // /ignore your teammates
                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isMember(player.getUniqueId())) {
                        player.sendMessage(teamChatFormat);
                    } else if (Foxtrot.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
                        player.sendMessage(teamChatSpyFormat);
                    }
                }

                // Log to our teamchat log.
                TeamActionTracker.logAction(playerTeam, TeamActionType.TEAM_CHAT, event.getPlayer().getName() + ": " + event.getMessage());
                Foxtrot.getInstance().getServer().getLogger().info("[Team Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
        }
    }

}