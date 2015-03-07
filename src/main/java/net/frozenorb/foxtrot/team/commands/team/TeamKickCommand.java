package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamKickCommand {

    @Command(names={ "team kick", "t kick", "f kick", "faction kick", "fac kick" }, permissionNode="")
    public static void teamKick(Player sender, @Parameter(name="player") OfflinePlayer target) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + target.getName() + " isn't on your team!");
            return;
        }

        if (team.isOwner(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if (team.isCaptain(target.getUniqueId())) {
            if (team.isCaptain(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "Only the owner can kick other captains!");
                return;
            }
        }

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.DARK_AQUA + target.getName() + " was kicked by " + sender.getName() + "!");
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Member Kicked: " + target.getName() + " [Kicked by: " + sender.getName() + "]");

        if (team.removeMember(target.getUniqueId())) {
            team.disband();
        } else {
            team.flagForSave();
        }

        FoxtrotPlugin.getInstance().getTeamHandler().setTeam(target.getUniqueId(), null);

        if (target.isOnline()) {
            FrozenNametagHandler.reloadPlayer(target.getPlayer());
            FrozenNametagHandler.reloadOthersFor(target.getPlayer());
        }
    }

}