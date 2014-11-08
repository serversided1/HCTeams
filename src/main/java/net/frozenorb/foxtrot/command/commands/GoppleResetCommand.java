package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GoppleResetCommand {

    @Command(names={ "GoppleReset" }, permissionNode="foxtrot.gopplereset")
    public static void goppleReset(Player sender, @Param(name="Target", defaultValue="self") String target) {
        if (target.equals("self")) {
            target = sender.getName();
        }

        FoxtrotPlugin.getInstance().getOppleMap().resetCooldown(target.toLowerCase());
        sender.sendMessage(ChatColor.RED + "Cooldown reset!");
    }

}