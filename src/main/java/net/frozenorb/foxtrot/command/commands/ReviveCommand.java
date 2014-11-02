package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ReviveCommand {

    @Command(names={ "Revive" }, permissionNode="op")
    public static void playSound(Player sender, @Param(name="Target") String target) {
        if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(target)) {
            FoxtrotPlugin.getInstance().getDeathbanMap().updateValue(target, 0L);
            sender.sendMessage(ChatColor.GREEN + "Revived " + target + "!");
        } else {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
        }
    }

}