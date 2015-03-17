package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReloadPrefixes {

    @Command(names={ "reloadprefixes" }, permissionNode="op")
    public static void reloadPrefixes(Player sender) {
        Foxtrot.getInstance().getServerHandler().loadPrefixes();
        sender.sendMessage(ChatColor.DARK_PURPLE + "Reloaded prefixes from file.");
    }

}