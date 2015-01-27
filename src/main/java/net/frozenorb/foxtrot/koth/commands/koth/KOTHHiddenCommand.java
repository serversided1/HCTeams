package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHHiddenCommand {

    @Command(names={ "KOTH Hidden" }, permissionNode="foxtrot.koth.admin")
    public static void kothHidden(Player sender, @Param(name="KOTH") KOTH target, @Param(name="Hidden") boolean hidden) {
        target.setHidden(hidden);
        sender.sendMessage(ChatColor.GRAY + "Set visibility for the " + target.getName() + " KOTH.");
    }

}