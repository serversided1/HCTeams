package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTimeCommand {

    @Command(names={ "KOTH Time" }, permissionNode="foxtrot.koth.admin")
    public static void kothTime(Player sender, @Parameter(name="koth") KOTH koth, @Parameter(name="time") int time) {
        koth.setCapTime(time);
        sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + koth.getName() + " KOTH.");
    }

}