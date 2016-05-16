package net.frozenorb.foxtrot.koth.commands.kothschedule;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHDisableSchedule {

    @Command(names = "KOTHSchedule Disable", permission = "foxtrot.koth.admin")
    public static void kothScheduleDisable(CommandSender sender) {
        Foxtrot.getInstance().getKOTHHandler().setScheduleEnabled(false);

        sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been " + ChatColor.RED + "disabled" + ChatColor.YELLOW + ".");
    }

}
