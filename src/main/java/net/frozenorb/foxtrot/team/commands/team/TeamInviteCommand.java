package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TeamInviteCommand {

    @Command(names={ "team invite", "t invite", "f invite", "faction invite", "fac invite", "team inv", "t inv", "f inv", "faction inv", "fac inv" }, permissionNode="")
    public static void teamInvite(Player sender, @Parameter(name="player") OfflinePlayer target) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Team.MAX_TEAM_SIZE) {
            sender.sendMessage(ChatColor.RED + "The max team size is " + Team.MAX_TEAM_SIZE + "!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (team.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + target.getName() + " is already on your team.");
            return;
        }

        if (team.getInvitations().contains(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
            return;
        }

        /*if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
            return;
        }*/

        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Player Invited: " + target.getName() + " [Invited by: " + sender.getName() + "]");
        team.getInvitations().add(target.getUniqueId());
        team.flagForSave();

        if (target.isOnline()) {
            Player targetPlayer = target.getPlayer();
            targetPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'.");
            targetPlayer.sendMessage(ChatColor.DARK_AQUA + "Type '" + ChatColor.YELLOW + "/team join " + team.getName() + ChatColor.DARK_AQUA + "' to join.");
        }

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + target.getName() + " has been invited to the team!");
            }
        }
    }

}