package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPTimeCommand {

    @Command(names={ "pvptimer time", "timer time", "pvp time" }, permissionNode="")
    public static void pvpTime(Player sender) {
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.getDurationBreakdown(FoxtrotPlugin.getInstance().getPvPTimerMap().getTimer(sender.getName()) - System.currentTimeMillis()) + " left on your PVP Timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
        }
    }

}