package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.listener.BorderListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class SetWorldBorderCommand {

    @Command(names={ "SetWorldBorder" }, permissionNode="op")
    public static void setWorldBorder(Player sender, @Param(name="Distance") int value) {
        BorderListener.BORDER_SIZE = value;
        sender.sendMessage(ChatColor.GRAY + "The world border is now set to " + BorderListener.BORDER_SIZE + " blocks.");
    }

}