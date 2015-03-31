package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHActivateCommand {

    @Command(names={ "KOTH Activate", "KOTH Active" }, permissionNode="foxtrot.koth")
    public static void kothActivate(Player sender, @Parameter(name="KOTH") KOTH target) {
        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (koth.isActive() && !koth.isHidden()) {
                sender.sendMessage(ChatColor.RED + "Another KOTH (" + koth.getName() + ") is already active.");
                return;
            }
        }
        
        target.activate();
        sender.sendMessage(ChatColor.GRAY + "Activated " + target.getName() + ".");
    }

}