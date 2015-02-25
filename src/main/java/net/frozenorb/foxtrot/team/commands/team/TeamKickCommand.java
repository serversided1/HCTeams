package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamKickCommand {

    @Command(names={ "team kick", "t kick", "f kick", "faction kick", "fac kick" }, permissionNode="")
    public static void teamKick(Player sender, @Param(name="player") String target) {
        Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayer(target);

        if (bukkitPlayer != null) {
            target = bukkitPlayer.getName();
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getName()) || team.isCaptain(sender.getName()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(target)) {
            sender.sendMessage(ChatColor.RED + target + " isn't on your team!");
            return;
        }

        if (team.isOwner(target)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if (team.isCaptain(target)) {
            if (team.isCaptain(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "Only the owner can kick other captains!");
                return;
            }
        }

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(target) + " was kicked by " + sender.getName() + "!");
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Member Kicked: " + target + " [Kicked by: " + sender.getName() + "]");

        if (team.removeMember(target)) {
            team.disband();
        } else {
            team.flagForSave();
        }

        FoxtrotPlugin.getInstance().getTeamHandler().setTeam(target, null);

        if (bukkitPlayer != null) {
            NametagManager.reloadPlayer(bukkitPlayer);
            NametagManager.sendTeamsToPlayer(bukkitPlayer);
        }
    }

}