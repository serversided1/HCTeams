package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class ToggleChatCommand {

    @Command(names={ "ToggleChat", "ToggleGlobalChat", "TC", "TGC" }, permissionNode="")
    public static void toggleChat(Player sender) {
        boolean val = !FoxtrotPlugin.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(sender.getName());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see global chat!");
        FoxtrotPlugin.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(sender.getName(), val);
    }

}