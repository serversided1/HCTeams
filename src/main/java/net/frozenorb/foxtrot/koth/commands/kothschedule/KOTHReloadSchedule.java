package net.frozenorb.foxtrot.koth.commands.kothschedule;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHReloadSchedule {

    @Command(names={ "KOTHSchedule Reload" }, permissionNode="foxtrot.koth.admin")
    public static void kothReloadSchedule(Player sender) {
        Foxtrot.getInstance().getKOTHHandler().loadSchedules();
        sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }

}