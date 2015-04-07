package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class TeamKickCommand {

    @Command(names={ "team kick", "t kick", "f kick", "faction kick", "fac kick" }, permissionNode="")
    public static void teamKick(Player sender, @Parameter(name="player") UUID player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't on your team!");
            return;
        }

        if (team.isOwner(player)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if (team.isCaptain(player)) {
            if (team.isCaptain(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "Only the owner can kick other captains!");
                return;
            }
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " was kicked by " + sender.getName() + "!");
        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Member Kicked: " + UUIDUtils.name(player) + " [Kicked by: " + sender.getName() + "]");

        if (team.removeMember(player)) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Foxtrot.getInstance().getTeamHandler().setTeam(player, null);
        Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);

        if (bukkitPlayer != null) {
            FrozenNametagHandler.reloadPlayer(bukkitPlayer);
            FrozenNametagHandler.reloadOthersFor(bukkitPlayer);
        }
    }

}