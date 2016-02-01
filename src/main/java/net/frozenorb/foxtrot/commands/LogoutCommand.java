package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LogoutCommand {

    @Command(names = {"Logout"}, permissionNode = "")
    public static void logout(Player sender) {

        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You can't log out while you're frozen!");
            return;
        }

        Foxtrot.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}