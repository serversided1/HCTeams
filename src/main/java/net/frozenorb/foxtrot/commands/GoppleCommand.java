package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.qlib.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GoppleCommand {

    @Command(names={ "Gopple", "Opple", "GoppleTime", "OppleTime", "GoppleTimer", "OppleTimer" }, permissionNode="")
    public static void gopple(Player sender) {
        if (FoxtrotPlugin.getInstance().getOppleMap().isOnCooldown(sender.getUniqueId())) {
            long millisLeft = FoxtrotPlugin.getInstance().getOppleMap().getCooldown(sender.getUniqueId()) - System.currentTimeMillis();
            sender.sendMessage(ChatColor.GOLD + "Gopple cooldown: " + ChatColor.WHITE + TimeUtils.getDurationBreakdown(millisLeft));
        } else {
            sender.sendMessage(ChatColor.RED + "No current gopple cooldown!");
        }
    }

}