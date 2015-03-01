package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GoppleResetCommand {

    @Command(names={ "GoppleReset" }, permissionNode="foxtrot.gopplereset")
    public static void goppleReset(Player sender, @Parameter(name="Target") String target) {
        FoxtrotPlugin.getInstance().getOppleMap().resetCooldown(target.toLowerCase());
        sender.sendMessage(ChatColor.RED + "Cooldown reset!");
    }

}