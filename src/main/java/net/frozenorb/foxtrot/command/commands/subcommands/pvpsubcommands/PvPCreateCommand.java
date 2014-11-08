package net.frozenorb.foxtrot.command.commands.subcommands.pvpsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class PvPCreateCommand {

    @Command(names={ "pvptimer create", "timer create", "pvp create" }, permissionNode="op")
    public static void pvpCreate(Player sender) {
        FoxtrotPlugin.getInstance().getPvPTimerMap().createTimer(sender.getName(), (int) TimeUnit.MINUTES.toSeconds(30));
        sender.sendMessage(ChatColor.RED + "You have 30 minutes of PVP Timer!");
    }

}