package net.frozenorb.foxtrot.command.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/23/2014.
 */
public class KOTHReloadSchedule {

    @Command(names={ "KOTH ReloadSchedule" }, permissionNode="foxtrot.koth.admin")
    public static void kothReloadSchedule(Player sender) {
        KOTHHandler.loadSchedules();
        KOTHScheduleCommand.kothSchedule(sender);
        sender.sendMessage(ChatColor.YELLOW + "Reloaded the KOTH schedule.");
    }

}