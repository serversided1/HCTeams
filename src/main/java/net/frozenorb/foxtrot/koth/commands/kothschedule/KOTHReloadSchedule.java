package net.frozenorb.foxtrot.koth.commands.kothschedule;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/23/2014.
 */
public class KOTHReloadSchedule {

    @Command(names={ "KOTHSchedule Reload" }, permissionNode="foxtrot.koth.admin")
    public static void kothReloadSchedule(Player sender) {
        KOTHHandler.loadSchedules();
        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }

}