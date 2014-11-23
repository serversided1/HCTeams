package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPEnableCommand {

    @Command(names={ "pvptimer enable", "timer enable", "pvp enable", "pvptimer remove", "timer remove", "pvp remove" }, permissionNode="")
    public static void pvpEnable(Player sender) {
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(sender.getName())) {
            FoxtrotPlugin.getInstance().getPvPTimerMap().removeTimer(sender.getName());
            sender.sendMessage(ChatColor.RED + "Your PVP Timer has been removed!");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
        }
    }

}