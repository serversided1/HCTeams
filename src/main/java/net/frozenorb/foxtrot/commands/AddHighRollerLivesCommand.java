package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class AddHighRollerLivesCommand {

    public static final int FRIEND_LIVE_COUNT = 5;
    public static final int SOULBOUND_LIVE_COUNT = 25;

    @Command(names={ "addHighRollerLives" }, permission="op")
    public static void addHighRollerLives(Player sender) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to add lives to all HighRollers? This action CANNOT be reversed. Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Set<UUID> highrollers = Foxtrot.getInstance().getServerHandler().getHighRollers();
                    int friendTotal = 0;
                    int soulboundTotal = 0;

                    for (UUID highroller : highrollers) {
                        Foxtrot.getInstance().getFriendLivesMap().setLives(highroller, Foxtrot.getInstance().getFriendLivesMap().getLives(highroller) + FRIEND_LIVE_COUNT);
                        Foxtrot.getInstance().getSoulboundLivesMap().setLives(highroller, Foxtrot.getInstance().getSoulboundLivesMap().getLives(highroller) + SOULBOUND_LIVE_COUNT);

                        friendTotal += FRIEND_LIVE_COUNT;
                        soulboundTotal += SOULBOUND_LIVE_COUNT;
                    }

                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Distributed " + friendTotal + " friend lives and " + soulboundTotal + " soulbound lives across " + highrollers.size() + " HighRollers.");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Life addition aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}