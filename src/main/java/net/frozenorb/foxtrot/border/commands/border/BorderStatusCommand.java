package net.frozenorb.foxtrot.border.commands.border;

import net.frozenorb.foxtrot.border.BorderThread;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;

public class BorderStatusCommand {

    @Command(names={ "border status" }, permissionNode="op")
    public static void borderStatus(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + BorderThread.getStateString());
        sender.sendMessage(ChatColor.GOLD + "Border thread last ticked: " + ChatColor.WHITE + new Date(BorderThread.getStateUpdated()) + " (" + (System.currentTimeMillis() - BorderThread.getStateUpdated()) + "ms ago)");
    }

}