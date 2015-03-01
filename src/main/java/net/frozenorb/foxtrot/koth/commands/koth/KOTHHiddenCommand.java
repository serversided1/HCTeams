package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHHiddenCommand {

    @Command(names={ "KOTH Hidden" }, permissionNode="foxtrot.koth.admin")
    public static void kothHidden(Player sender, @Parameter(name="KOTH") KOTH target, @Parameter(name="Hidden") boolean hidden) {
        target.setHidden(hidden);
        sender.sendMessage(ChatColor.GRAY + "Set visibility for the " + target.getName() + " KOTH.");
    }

}