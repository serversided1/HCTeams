package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by macguy8 on 11/23/2014.
 */
public class KOTHScheduleCommand {

    @Command(names={ "KOTH Schedule" }, permissionNode="")
    public static void kothSchedule(Player sender) {
        int sent = 0;

        for (Map.Entry<Integer, String> entry : FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHSchedule().entrySet()) {
            KOTH resolved = FoxtrotPlugin.getInstance().getKOTHHandler().getKOTH(entry.getValue());

            if (resolved == null || resolved.isHidden()) {
                continue;
            }

            sent++;
            Calendar activationTime = Calendar.getInstance();

            activationTime.set(Calendar.HOUR_OF_DAY, entry.getKey());
            activationTime.set(Calendar.MINUTE, 0);
            activationTime.set(Calendar.SECOND, 0);
            activationTime.set(Calendar.MILLISECOND, 0);

            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + DateFormat.getTimeInstance(DateFormat.SHORT).format(activationTime.getTime()) + ChatColor.GOLD + ".");
        }

        if (sent == 0) {
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.RED + "KOTH Schedule: " + ChatColor.YELLOW + "Undefined");
        } else {
            sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()) + ChatColor.GOLD + ".");
        }
    }

}