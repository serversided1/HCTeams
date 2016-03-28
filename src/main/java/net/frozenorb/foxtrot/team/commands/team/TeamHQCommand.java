package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.mBasic.CommandSystem.Commands.Freeze;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class TeamHQCommand {

    @Command(names={ "team hq", "t hq", "f hq", "faction hq", "fac hq", "team home", "t home", "f home", "faction home", "fac home", "home", "hq" }, permissionNode="")
    public static void teamHQ(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
            return;
        }

        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "HQ not set.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters during the End of the World!");
            return;
        }

        if (Freeze.isFrozen(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while you're frozen!");
            return;
        }

        // Ask player if they want to initiate TP even though they will lose their pvp timer
        // This is only applicable to players in the spawn, since it is an instant teleport.
        // If they aren't in spawn, they can always move an inch to cancel their pending teleport.

        Team inClaim = LandBoard.getInstance().getTeam(sender.getLocation());

        if (inClaim != null && inClaim.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && Foxtrot.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            String prompt = "§cTeleporting to your teams HQ will remove your PvP Timer. §eAre you sure? Type in yes or no";

            conversationBoolean(sender, prompt, toContinue -> {
                if (toContinue) {
                    Foxtrot.getInstance().getServerHandler().beginHQWarp(sender, team, 10);
                } else {
                    sender.sendMessage(ChatColor.RED + "Teleport cancelled.");
                }
            });
        } else {
            Foxtrot.getInstance().getServerHandler().beginHQWarp(sender, team, 10);
        }
    }

    private static void conversationBoolean(Player p, String prompt, Callback<Boolean> callback) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true)
                .withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String newName) {
                callback.callback(newName.equalsIgnoreCase("yes")); // true if yes, no if anything else
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }
}