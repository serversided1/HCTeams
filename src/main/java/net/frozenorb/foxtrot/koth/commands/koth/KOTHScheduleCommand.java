package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHScheduledTime;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class KOTHScheduleCommand {

    public static final DateFormat KOTH_DATE_FORMAT = new SimpleDateFormat("EEE hha");

    // Make this pretty.
    @Command(names={ "KOTH Schedule" }, permissionNode="")
    public static void kothSchedule(Player sender) {
        int sent = 0;

        for (Map.Entry<KOTHScheduledTime, String> entry : Foxtrot.getInstance().getKOTHHandler().getKOTHSchedule().entrySet()) {
            KOTH resolved = Foxtrot.getInstance().getKOTHHandler().getKOTH(entry.getValue());

            if (resolved == null || resolved.isHidden()) {
                continue;
            }

            if (sent > 5) {
                break;
            }

            sent++;
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + ChatColor.GOLD + ".");
        }

        if (sent == 0) {
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.RED + "KOTH Schedule: " + ChatColor.YELLOW + "Undefined");
        } else {
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(new Date()) + ChatColor.GOLD + ".");
        }
    }

}