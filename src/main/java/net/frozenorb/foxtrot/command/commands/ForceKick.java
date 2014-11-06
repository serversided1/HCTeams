package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class ForceKick {

    @Command(names={ "forcekick" }, permissionNode="op")
    public static void forceKick(Player player, @Param(name="Player") String name) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(name);

        if (team == null) {
            player.sendMessage(ChatColor.RED + name + " is not on a team!");
            return;
        }

        if (team.removeMember(name)) {
            FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(name);
            FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(team.getName());
            LandBoard.getInstance().clear(team);
        }

        FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(name);

        player.sendMessage(ChatColor.GRAY + "Force-kicked " + name + " from their team, " + team.getFriendlyName() + ".");
    }

}