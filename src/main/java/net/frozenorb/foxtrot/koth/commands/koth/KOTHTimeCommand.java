package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTimeCommand {

    @Command(names={ "KOTH Time" }, permissionNode="foxtrot.koth.admin")
    public static void kothTime(Player sender, @Parameter(name="KOTH") KOTH target, @Parameter(name="Time") int capTime) {
        target.setCapTime(capTime);
        sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + target.getName() + " KOTH.");
    }

}