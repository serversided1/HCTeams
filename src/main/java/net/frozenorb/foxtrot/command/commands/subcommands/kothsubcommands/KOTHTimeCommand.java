package net.frozenorb.foxtrot.command.commands.subcommands.kothsubcommands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHTimeCommand {

    @Command(names={ "KOTH Time" }, permissionNode="foxtrot.koth.admin")
    public static void kothSetCapTime(Player sender, @Param(name="KOTH") KOTH target, @Param(name="Time") int capTime) {
        target.setCapTime(capTime);
        sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + target.getName() + " KOTH.");
    }

}