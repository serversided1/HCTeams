package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamInviteCommand {

    @Command(names={ "team invite", "t invite", "f invite", "faction invite", "fac invite", "team inv", "t inv", "f inv", "faction inv", "fac inv" }, permissionNode="")
    public static void teamInvite(Player sender, @Parameter(name="player") UUID player, @Parameter(name="override?", defaultValue="something-not-override") String override) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

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

        if (team.isMember(player)) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " is already on your team.");
            return;
        }

        if (team.getInvitations().contains(player)) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().getBetrayers().contains(player) && !override.equalsIgnoreCase("override")) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Warning: " + ChatColor.RED + UUIDUtils.name(player) + " has previously betrayed another team. Are you sure you want to invite " + UUIDUtils.name(player) + "? Type '/t invite " + UUIDUtils.name(player) + " override' to ignore this warning.");
            return;
        }

        /*if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
            return;
        }*/

        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Player Invited: " + UUIDUtils.name(player) + " [Invited by: " + sender.getName() + "]");
        team.getInvitations().add(player);
        team.flagForSave();

        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'.");
            bukkitPlayer.sendMessage(ChatColor.DARK_AQUA + "Type '" + ChatColor.YELLOW + "/team join " + team.getName() + ChatColor.DARK_AQUA + "' to join.");
        }

        team.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " has been invited to the team!");
    }

}