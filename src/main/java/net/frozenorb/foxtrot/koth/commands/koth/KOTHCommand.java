package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.annotations.Command;
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
        for (KOTH koth : FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHs()) {
            if (koth.isHidden()) {
                continue;
            }

            if (koth.isActive()) {
                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + koth.getName() + ChatColor.GOLD + " can be contested now.");
                return;
            }
        }

        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);

        for (Map.Entry<Integer, String> entry : FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHSchedule().entrySet()) {
            if (entry.getKey() > hour) {
                Calendar activationTime = Calendar.getInstance();

                activationTime.set(Calendar.HOUR_OF_DAY, entry.getKey());
                activationTime.set(Calendar.MINUTE, 0);
                activationTime.set(Calendar.SECOND, 0);
                activationTime.set(Calendar.MILLISECOND, 0);

                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + DateFormat.getTimeInstance(DateFormat.SHORT).format(activationTime.getTime()) + ChatColor.GOLD + ".");
                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + ChatColor.GOLD + ".");
                return;
            }
        }

        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.RED + "Next KOTH: " + ChatColor.YELLOW + "Undefined");
    }

}