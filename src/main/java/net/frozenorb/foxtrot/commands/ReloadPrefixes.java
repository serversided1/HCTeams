package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;

public class ReloadPrefixes {

    @Command(names={ "reloadprefixes" }, permission="op")
    public static void reloadPrefixes(Player sender) {
        Foxtrot.getInstance().getChatHandler().reloadCustomPrefixes();
        sender.sendMessage(ChatColor.DARK_PURPLE + "Reloaded prefixes from file.");
    }

}