package net.frozenorb.foxtrot.koth.commands.koth;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHListCommand {

    @Command(names={ "KOTH List" }, permissionNode="foxtrot.koth")
    public static void kothList(Player sender) {
        for (KOTH koth : FoxtrotPlugin.getInstance().getKOTHHandler().getKOTHs()) {
            sender.sendMessage((koth.isHidden() ? ChatColor.DARK_GRAY + "[H] " : "") + (koth.isActive() ? ChatColor.GREEN : ChatColor.RED) + koth.getName() + ChatColor.WHITE + " - " + ChatColor.GRAY + TimeUtils.getMMSS(koth.getRemainingCapTime()) + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + TimeUtils.getMMSS(koth.getCapTime()) + " " + ChatColor.WHITE + "- " + ChatColor.GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()) + (koth.isHidden() ? "" : ChatColor.WHITE + " - " + ChatColor.GRAY + "Level " + koth.getLevel()));
        }
    }

}