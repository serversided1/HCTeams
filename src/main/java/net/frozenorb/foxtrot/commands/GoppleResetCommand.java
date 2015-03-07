package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class GoppleResetCommand {

    @Command(names={ "GoppleReset" }, permissionNode="foxtrot.gopplereset")
    public static void goppleReset(Player sender, @Parameter(name="Target") OfflinePlayer target) {
        FoxtrotPlugin.getInstance().getOppleMap().resetCooldown(target.getUniqueId());
        sender.sendMessage(ChatColor.RED + "Cooldown reset!");
    }

}