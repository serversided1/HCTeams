package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDeactivateCommand {

    @Command(names={ "KOTH Deactivate", "KOTH Inactive" }, permissionNode="foxtrot.koth")
    public static void kothDectivate(Player sender, @Parameter(name="KOTH") KOTH target) {
        target.deactivate();
        sender.sendMessage(ChatColor.GRAY + "Deactivated " + target.getName() + " KOTH.");
    }

}