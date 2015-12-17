package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHListCommand {

    @Command(names={ "KOTH List" }, permissionNode="foxtrot.koth")
    public static void kothList(Player sender) {
        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            sender.sendMessage((koth.isHidden() ? ChatColor.DARK_GRAY + "[H] " : "") + (koth.isActive() ? ChatColor.GREEN : ChatColor.RED) + koth.getName() + ChatColor.WHITE + " - " + ChatColor.GRAY + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()) + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + TimeUtils.formatIntoMMSS(koth.getCapTime()) + " " + ChatColor.WHITE + "- " + ChatColor.GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()));
        }
    }

}