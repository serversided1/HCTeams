package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHDeactivateCommand {

    @Command(names={ "KOTH Deactivate", "KOTH Inactive" }, permission="foxtrot.koth")
    public static void kothDectivate(CommandSender sender, @Param(name="koth") KOTH koth) {
        koth.deactivate();
        sender.sendMessage(ChatColor.GRAY + "Deactivated " + koth.getName() + " KOTH.");
    }

}