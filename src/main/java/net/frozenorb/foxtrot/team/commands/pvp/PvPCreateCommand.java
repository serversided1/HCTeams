package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PvPCreateCommand {

    @Command(names={ "pvptimer create", "timer create", "pvp create" }, permissionNode="op")
    public static void pvpCreate(Player sender, @Param(name="target", defaultValue="self") Player target) {
        FoxtrotPlugin.getInstance().getPvPTimerMap().createTimer(target.getName(), (int) TimeUnit.MINUTES.toSeconds(30));
        target.sendMessage(ChatColor.YELLOW + "You have 30 minutes of PVP Timer!");

        if (sender != target) {
            sender.sendMessage(ChatColor.YELLOW + "Gave 30 minutes of PVP Timer to " + target.getName() + ".");
        }
    }

}