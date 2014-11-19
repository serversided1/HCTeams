package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class ForceKickCommand {

    @Command(names={ "forcekick" }, permissionNode="op")
    public static void forceKick(Player sender, @Param(name="player") OfflinePlayer player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on a team!");
            return;
        }

        if (team.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + player.getName() + "'s team has one member. Please use /forcedisband to perform this action.");
            return;
        }

        team.removeMember(player.getName());
        FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeamMap().remove(player.getName());

        sender.sendMessage(ChatColor.GRAY + "Force-kicked " + player.getName() + " from their team, " + team.getFriendlyName() + ".");
    }

}