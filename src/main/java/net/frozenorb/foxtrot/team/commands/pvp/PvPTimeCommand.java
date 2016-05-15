package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPTimeCommand {

    @Command(names={ "pvptimer time", "timer time", "pvp time" }, permission="")
    public static void pvpTime(Player sender) {
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.formatIntoMMSS(Foxtrot.getInstance().getPvPTimerMap().getSecondsRemaining(sender.getUniqueId())) + " left on your PVP Timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
        }
    }

}