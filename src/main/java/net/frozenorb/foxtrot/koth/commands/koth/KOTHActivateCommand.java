package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHActivateCommand {

    @Command(names={ "KOTH Activate", "KOTH Active" }, permission="foxtrot.koth")
    public static void kothActivate(Player sender, @Param(name="koth") KOTH koth) {
        // Don't start a KOTH if another one is active.
        for (KOTH otherKoth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (otherKoth.isActive()) {
                sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
                return;
            }
        }

        koth.activate();
        sender.sendMessage(ChatColor.GRAY + "Activated " + koth.getName() + ".");
    }

}