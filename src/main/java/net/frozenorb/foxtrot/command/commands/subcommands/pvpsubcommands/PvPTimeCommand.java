package net.frozenorb.foxtrot.command.commands.subcommands.pvpsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
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