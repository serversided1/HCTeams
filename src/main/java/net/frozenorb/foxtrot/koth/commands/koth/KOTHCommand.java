package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHScheduledTime;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class KOTHCommand {

    // Make this pretty.
    @Command(names={ "KOTH", "KOTH Next", "KOTH Info", "KOTH" }, permissionNode="")
    public static void kothSchedule(Player sender) {
        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (!koth.isHidden() && koth.isActive()) {
                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + koth.getName() + ChatColor.GOLD + " can be contested now.");
                return;
            }
        }

        Date now = new Date();

        for (Map.Entry<KOTHScheduledTime, String> entry : Foxtrot.getInstance().getKOTHHandler().getKOTHSchedule().entrySet()) {
            if (entry.getKey().toDate().after(now)) {
                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + DateFormat.getTimeInstance(DateFormat.SHORT).format(entry.getKey().toDate()) + ChatColor.GOLD + ".");
                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + DateFormat.getTimeInstance(DateFormat.SHORT).format(now) + ChatColor.GOLD + ".");
                return;
            }
        }

        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.RED + "Next KOTH: " + ChatColor.YELLOW + "Undefined");
    }

}