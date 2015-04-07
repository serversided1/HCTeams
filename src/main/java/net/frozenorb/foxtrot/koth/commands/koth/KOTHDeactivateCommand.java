package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDeactivateCommand {

    @Command(names={ "KOTH Deactivate", "KOTH Inactive" }, permissionNode="foxtrot.koth")
    public static void kothDectivate(Player sender, @Parameter(name="koth") KOTH koth) {
        koth.deactivate();
        sender.sendMessage(ChatColor.GRAY + "Deactivated " + koth.getName() + " KOTH.");
    }

}