package net.frozenorb.foxtrot.command.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TeamInviteCommand {

    @Command(names={ "team invite", "t invite", "f invite", "faction invite", "fac invite" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="player") OfflinePlayer target) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Team.MAX_TEAM_SIZE) {
            sender.sendMessage(ChatColor.RED + "The max team size is " + Team.MAX_TEAM_SIZE + "!");
            return;
        }

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            if (!team.isMember(target.getName())) {
                if (team.getInvitations().contains(target.getName())) {
                    sender.sendMessage(ChatColor.RED + "That player has already been invited.");
                    return;
                }

                if (team.isRaidable()){
                    sender.sendMessage(ChatColor.RED + "You may not invite players if your team is raidable! You must boost your DTR!");
                    return;
                }

                FactionActionTracker.logAction(team, "actions", "Player Invited: " + target.getName() + " [Invited by: " + sender.getName() + "]");
                team.getInvitations().add(target.getName());
                team.flagForSave();

                if (target.isOnline()) {
                    Player targetPlayer = target.getPlayer();
                    targetPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getFriendlyName() + ChatColor.DARK_AQUA + "'.");
                    targetPlayer.sendMessage(ChatColor.DARK_AQUA + "Type '" + ChatColor.YELLOW + "/f join " + team.getFriendlyName() + ChatColor.DARK_AQUA + "' to join.");
                }

                sender.sendMessage("§e" + target.getName() + " has been invited to the team!");
            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Player is already on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
	}

}