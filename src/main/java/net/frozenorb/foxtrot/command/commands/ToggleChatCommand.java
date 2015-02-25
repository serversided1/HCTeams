package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleChatCommand {

    @Command(names={ "ToggleChat", "ToggleGlobalChat", "TGC" }, permissionNode="")
    public static void toggleChat(Player sender) {
        boolean val = !FoxtrotPlugin.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(sender.getName());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see global chat!");
        FoxtrotPlugin.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(sender.getName(), val);
    }

}