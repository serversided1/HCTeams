package net.frozenorb.foxtrot.koth.commands.kothschedule;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class KOTHRegenerateSchedule {

    @Command(names = {"KOTHSchedule Regenerate", "KOTHSchedule Regen"}, permission = "foxtrot.koth.admin", async = true)
    public static void kothScheduleEnable(CommandSender sender) {
        File kothSchedule = new File(Foxtrot.getInstance().getDataFolder(), "kothSchedule.json");

        if (kothSchedule.delete()) {
            Foxtrot.getInstance().getKOTHHandler().loadSchedules();

            sender.sendMessage(ChatColor.YELLOW + "The KOTH schedule has been regenerated.");
        } else {
            sender.sendMessage(ChatColor.RED + "Couldn't delete KOTH schedule file.");
        }
    }

}
