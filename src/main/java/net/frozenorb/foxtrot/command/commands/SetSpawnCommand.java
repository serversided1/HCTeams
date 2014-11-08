package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class SetSpawnCommand {

    @Command(names={ "SetSpawn" }, permissionNode="op")
    public static void setSpawn(Player sender) {
        ConversationFactory factory = new ConversationFactory(FoxtrotPlugin.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to set spawn here? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Location l = ((Player) cc.getForWhom()).getLocation();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Spawn set!");
                    ((Player) cc.getForWhom()).getWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Spawn setting cancelled.");
                    return Prompt.END_OF_CONVERSATION;

                }
                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

}