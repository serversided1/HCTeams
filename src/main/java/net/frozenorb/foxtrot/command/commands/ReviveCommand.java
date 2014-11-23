package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReviveCommand {

    @Command(names={ "Revive" }, permissionNode="op")
    public static void playSound(CommandSender sender, @Param(name="Target") String target) {
        if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(target)) {
            FoxtrotPlugin.getInstance().getDeathbanMap().revive(target);
            sender.sendMessage(ChatColor.GREEN + "Revived " + target + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }

}