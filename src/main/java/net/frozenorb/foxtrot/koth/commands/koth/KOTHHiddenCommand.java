package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHHiddenCommand {

    @Command(names={ "KOTH Hidden" }, permissionNode="foxtrot.koth.admin")
    public static void kothHidden(Player sender, @Parameter(name="koth") KOTH koth, @Parameter(name="hidden") boolean hidden) {
        koth.setHidden(hidden);
        sender.sendMessage(ChatColor.GRAY + "Set visibility for the " + koth.getName() + " KOTH.");
    }

}