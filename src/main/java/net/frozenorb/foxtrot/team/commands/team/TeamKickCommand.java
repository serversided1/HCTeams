package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamKickCommand {

    @Command(names={ "team kick", "t kick", "f kick", "faction kick", "fac kick" }, permissionNode="")
    public static void teamKick(Player sender, @Parameter(name="player") OfflinePlayer target) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getName()) || team.isCaptain(sender.getName()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(target.getName())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " isn't on your team!");
            return;
        }

        if (team.isOwner(target.getName())) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if (team.isCaptain(target.getName())) {
            if (team.isCaptain(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "Only the owner can kick other captains!");
                return;
            }
        }

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player)) {
                player.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(target.getName()) + " was kicked by " + sender.getName() + "!");
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Member Kicked: " + target.getName() + " [Kicked by: " + sender.getName() + "]");

        if (team.removeMember(target.getName())) {
            team.disband();
        } else {
            team.flagForSave();
        }

        FoxtrotPlugin.getInstance().getTeamHandler().setTeam(target.getName(), null);

        if (target.isOnline()) {
            NametagManager.reloadPlayer(target.getPlayer());
            NametagManager.sendTeamsToPlayer(target.getPlayer());
        }
    }

}