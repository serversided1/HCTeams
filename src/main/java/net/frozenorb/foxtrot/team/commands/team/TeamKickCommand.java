package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class TeamKickCommand {

    @Command(names={ "team kick", "t kick", "f kick", "faction kick", "fac kick" }, permissionNode="")
    public static void teamKick(Player sender, @Parameter(name="player") UUID target) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(target)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(target) + " isn't on your team!");
            return;
        }

        if (team.isOwner(target)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if (team.isCaptain(target)) {
            if (team.isCaptain(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "Only the owner can kick other captains!");
                return;
            }
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(target) + " was kicked by " + sender.getName() + "!");
        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Member Kicked: " + UUIDUtils.name(target) + " [Kicked by: " + sender.getName() + "]");

        if (team.removeMember(target)) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Foxtrot.getInstance().getTeamHandler().setTeam(target, null);
        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(target);

        if (bukkitPlayer != null) {
            FrozenNametagHandler.reloadPlayer(bukkitPlayer);
            FrozenNametagHandler.reloadOthersFor(bukkitPlayer);
        }
    }

}