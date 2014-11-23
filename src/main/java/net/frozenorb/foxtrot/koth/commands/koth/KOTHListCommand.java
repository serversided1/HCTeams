package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class KOTHListCommand {

    @Command(names={ "KOTH List" }, permissionNode="foxtrot.koth")
    public static void kothList(Player sender) {
        for (KOTH koth : KOTHHandler.getKOTHs()) {
            sender.sendMessage((koth.isActive() ? ChatColor.GREEN : ChatColor.RED) + koth.getName() + " KOTH " + ChatColor.WHITE + "- " + ChatColor.GRAY + koth.getRemainingCapTime() + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + koth.getCapTime() + " " + ChatColor.WHITE + "- " + ChatColor.GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()) + ChatColor.WHITE + " - " + ChatColor.GRAY + "Tier " + koth.getLevel());
        }
    }

}