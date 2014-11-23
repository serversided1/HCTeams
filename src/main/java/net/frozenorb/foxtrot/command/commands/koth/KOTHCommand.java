package net.frozenorb.foxtrot.command.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by macguy8 on 11/23/2014.
 */
public class KOTHCommand {

    @Command(names={ "KOTH", "KOTH Next", "KOTH Info", "KOTH" }, permissionNode="foxtrot.koth")
    public static void kothSchedule(Player sender) {
        for (KOTH koth : KOTHHandler.getKOTHs()) {
            if (koth.isActive()) {
                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + koth.getName() + ChatColor.GOLD + " can be contested now.");
                return;
            }
        }

        Calendar date = Calendar.getInstance();
        int hour = date.get(Calendar.HOUR_OF_DAY);

        for (Map.Entry<Integer, String> entry : KOTHHandler.getKothSchedule().entrySet()) {
            if (entry.getKey() > hour) {
                Calendar activationTime = Calendar.getInstance();

                activationTime.set(Calendar.HOUR_OF_DAY, entry.getKey());
                activationTime.set(Calendar.MINUTE, 0);
                activationTime.set(Calendar.SECOND, 0);
                activationTime.set(Calendar.MILLISECOND, 0);

                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + (new SimpleDateFormat()).format(activationTime.getTime()) + ChatColor.GOLD + ".");
                sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + (new SimpleDateFormat()).format(new Date()) + ChatColor.GOLD + ".");
                return;
            }
        }

        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.RED + "Next KOTH: " + ChatColor.YELLOW + "Undefined");
    }

}