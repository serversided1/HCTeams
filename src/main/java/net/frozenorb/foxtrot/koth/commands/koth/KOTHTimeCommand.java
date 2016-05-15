package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTimeCommand {

    @Command(names={ "KOTH Time" }, permission="foxtrot.koth.admin")
    public static void kothTime(Player sender, @Param(name="koth") KOTH koth, @Param(name="time") float time) {
        if (time > 20F) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "This command was changed! The time parameter is now in minutes, not seconds. For example, to set a KOTH's capture time to 20 minutes 30 seconds, use /koth time 20.5");
        }

        koth.setCapTime((int) (time * 60F));
        sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + koth.getName() + " KOTH.");
    }

}