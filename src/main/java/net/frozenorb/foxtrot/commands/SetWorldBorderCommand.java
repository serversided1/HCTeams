package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetWorldBorderCommand {

    @Command(names={ "SetWorldBorder" }, permissionNode="op")
    public static void setWorldBorder(Player sender, @Parameter(name="Distance") int value) {
        BorderListener.BORDER_SIZE = value;
        sender.sendMessage(ChatColor.GRAY + "The world border is now set to " + BorderListener.BORDER_SIZE + " blocks.");
    }

}