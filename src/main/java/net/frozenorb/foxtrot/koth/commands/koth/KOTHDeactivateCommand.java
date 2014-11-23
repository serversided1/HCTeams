package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHDeactivateCommand {

    @Command(names={ "KOTH Deactivate", "KOTH Inactive" }, permissionNode="foxtrot.koth")
    public static void kothDectivate(Player sender, @Param(name="KOTH") KOTH target) {
        target.deactivate();
        sender.sendMessage(ChatColor.GRAY + "Deactivated " + target.getName() + " KOTH.");
    }

}