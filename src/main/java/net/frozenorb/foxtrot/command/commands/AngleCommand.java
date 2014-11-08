package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AngleCommand {

    @Command(names={ "Angle" }, permissionNode="")
    public static void angle(Player sender) {
        sender.sendMessage(ChatColor.YELLOW.toString() + sender.getLocation().getYaw() + " yaw, " + sender.getLocation().getPitch() + " pitch");
    }

}