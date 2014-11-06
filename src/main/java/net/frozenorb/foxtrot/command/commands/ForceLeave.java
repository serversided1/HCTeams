package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 10/31/2014.
 */
public class ForceLeave {

    @Command(names={ "forceleave" }, permissionNode="op")
    public static void forceLeave(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        team.removeMember(player.getName());
        team.setOwner(null);
        FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(player.getName());
        player.sendMessage(ChatColor.GRAY + "Force-left your team.");
    }

}