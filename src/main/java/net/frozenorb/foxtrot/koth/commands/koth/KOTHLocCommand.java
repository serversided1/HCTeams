package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHLocCommand {

    @Command(names={ "KOTH loc" }, permissionNode="foxtrot.koth.admin")
    public static void kothLoc(Player sender, @Param(name="KOTH") KOTH target) {
        target.setLocation(sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Set cap location for the " + target.getName() + " KOTH.");
    }

}