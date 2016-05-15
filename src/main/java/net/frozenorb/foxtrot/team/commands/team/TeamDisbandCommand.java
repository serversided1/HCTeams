package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamDisbandCommand {

    @Command(names={ "team disband", "t disband", "f disband", "faction disband", "fac disband" }, permission="")
    public static void teamDisband(Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null){
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the leader of the team to disband it!");
            return;
        }

        if (team.isRaidable()) {
            player.sendMessage(ChatColor.RED + "You cannot disband your team while raidable.");
            return;
        }

        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getName() + " has disbanded the team.");
        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Team disbanded. [Disbanded by: " + player.getName() + "]");
        team.disband();
    }

}