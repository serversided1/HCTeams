package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;

public class ReloadMapConfigCommand {

    @Command(names={ "reloadMapConfig" }, permission="op")
    public static void reloadMapConfig(Player sender) {
        Foxtrot.getInstance().getMapHandler().reloadConfig();
        sender.sendMessage(ChatColor.DARK_PURPLE + "Reloaded mapInfo.json from file.");
    }

}