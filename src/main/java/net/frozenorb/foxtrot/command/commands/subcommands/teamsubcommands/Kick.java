package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class Kick {

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

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            if (team.isMember(target)) {
                if (team.isOwner(target)) {
                    sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
                    return;
                }

                if (team.isCaptain(target)) {
                    if (team.isCaptain(sender.getName())) {
                        sender.sendMessage(ChatColor.RED + "Only the leader can kick other captains!");
                        return;
                    }
                }

                for (Player pl : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (team.isMember(pl)) {
                        pl.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(target) + " was kicked by " + sender.getName() + "!");
                    }
                }

                FactionActionTracker.logAction(team, "actions", "Member Kicked: " + target + " [Kicked by: " + sender.getName() + "]");

                if (team.removeMember(target)) {
                    FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(team.getName());
                }

                FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(target);

                if (bukkitPlayer != null) {
                    NametagManager.reloadPlayer(bukkitPlayer);
                    NametagManager.sendTeamsToPlayer(bukkitPlayer);
                }
            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
	}

}