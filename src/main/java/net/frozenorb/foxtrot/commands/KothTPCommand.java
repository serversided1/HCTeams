package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KothTPCommand {

    @Command(names = {"kothtp"}, permissionNode = "worldedit.*")
    public static void kothtp(Player sender) {
        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (koth.isActive() && !koth.isHidden()) {
                sender.teleport(koth.getCapLocation().toLocation(sender.getWorld()));
                sender.sendMessage(ChatColor.GREEN + "Teleported you to " + ChatColor.YELLOW + koth.getName() + ChatColor.GREEN + ".");
                return;
            }
        }
    }

}
