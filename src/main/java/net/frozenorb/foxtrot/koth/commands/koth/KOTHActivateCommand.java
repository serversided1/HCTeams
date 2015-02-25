package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHActivateCommand {

    @Command(names={ "KOTH Activate", "KOTH Active" }, permissionNode="foxtrot.koth")
    public static void kothActivate(Player sender, @Param(name="KOTH") KOTH target) {
        target.activate();
        sender.sendMessage(ChatColor.GRAY + "Activated " + target.getName() + ".");
    }

}