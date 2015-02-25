package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaveCommand {

    @Command(names={ "forceleave" }, permissionNode="op")
    public static void forceLeave(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        team.removeMember(player.getName());
        FoxtrotPlugin.getInstance().getTeamHandler().setTeam(player.getName(), null);
        player.sendMessage(ChatColor.YELLOW + "Force left your team.");
    }

}