package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by chasechocolate.
 */
public class ForceDisband {

    @Command(names={ "forcedisband" }, permissionNode="op")
    public static void forceDisband(Player sender, @Param(name="team") Team target) {
        for (Player online : target.getOnlineMembers()) {
            online.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force-disbanded the team.");
        }

        FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(target.getName());
        sender.sendMessage(ChatColor.GRAY + "Force-disbanded the faction " + target.getFriendlyName() + ".");
    }

}