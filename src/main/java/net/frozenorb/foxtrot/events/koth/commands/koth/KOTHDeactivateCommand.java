package net.frozenorb.foxtrot.events.koth.commands.koth;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class KOTHDeactivateCommand {

    @Command(names={ "KOTH Deactivate", "KOTH Inactive", "event deactivate" }, permission="foxtrot.koth.admin")
    public static void kothDectivate(CommandSender sender, @Param(name="koth") Event koth) {
        koth.deactivate();
        sender.sendMessage(ChatColor.GRAY + "Deactivated " + koth.getName() + " event.");
    }

}
