package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleChatCommand {

    @Command(names={ "ToggleChat", "ToggleGlobalChat", "TGC" }, permission="")
    public static void toggleChat(Player sender) {
        boolean val = !Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see global chat!");
        Foxtrot.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(sender.getUniqueId(), val);
    }

}