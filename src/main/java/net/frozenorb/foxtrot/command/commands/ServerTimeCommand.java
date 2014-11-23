package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;

/**
 * Created by macguy8 on 11/22/2014.
 */
public class ServerTimeCommand {

    @Command(names={ "ServerTime" }, permissionNode="")
    public static void serverTime(Player sender) {
        sender.sendMessage(ChatColor.YELLOW.toString() + "It is " + ChatColor.LIGHT_PURPLE + (new Date().toString()) + ChatColor.YELLOW + ".");
    }

}
