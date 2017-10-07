package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;

public class GoppleCommand {

    @Command(names={ "Gopple", "Opple", "GoppleTime", "OppleTime", "GoppleTimer", "OppleTimer" }, permission="")
    public static void gopple(Player sender) {
        if (Foxtrot.getInstance().getOppleMap().isOnCooldown(sender.getUniqueId())) {
            long millisLeft = Foxtrot.getInstance().getOppleMap().getCooldown(sender.getUniqueId()) - System.currentTimeMillis();
            sender.sendMessage(ChatColor.GOLD + "Gopple cooldown: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString((int) millisLeft / 1000));
        } else {
            sender.sendMessage(ChatColor.RED + "No current gopple cooldown!");
        }
    }

}