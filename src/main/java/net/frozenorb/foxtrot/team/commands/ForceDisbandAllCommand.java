package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ForceDisbandAllCommand {

    @Command(names={ "forcedisbandall" }, permissionNode="op")
    public static void forceDisbandAll(Player sender) {
        ConversationFactory factory = new ConversationFactory(FoxtrotPlugin.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return (ChatColor.GREEN  + "Are you sure you want to disband all teams? Type §byes§a to confirm or §cno§a to quit.");
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    List<Team> teams = new ArrayList<>();

                    for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
                        teams.add(team);
                    }

                    for (Team team : teams) {
                        team.disband();
                    }

                    FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been forcibly disbanded!");
                    return (Prompt.END_OF_CONVERSATION);
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Disbanding cancelled.");
                    return (Prompt.END_OF_CONVERSATION);
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return (Prompt.END_OF_CONVERSATION);
            }

        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}