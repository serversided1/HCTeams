package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHLocCommand {

    @Command(names={ "KOTH loc" }, permissionNode="foxtrot.koth.admin")
    public static void kothLoc(Player sender, @Parameter(name="KOTH") KOTH target) {
        target.setLocation(sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Set cap location for the " + target.getName() + " KOTH.");
    }

}