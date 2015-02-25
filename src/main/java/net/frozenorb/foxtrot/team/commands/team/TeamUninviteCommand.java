package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUninviteCommand {

    @Command(names={ "team uninvite", "t uninvite", "f uninvite", "faction uninvite", "fac uninvite", "team revoke", "t revoke", "f revoke", "faction revoke", "fac revoke" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="all | player") String name) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            if (name.equalsIgnoreCase("all")) {
                team.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                String remove = null;

                for (String possibleName : team.getInvitations()) {
                    if (possibleName.equalsIgnoreCase(name)) {
                        remove = possibleName;
                        break;
                    }
                }

                if (remove != null) {
                    TeamActionTracker.logActionAsync(team, TeamActionType.GENERAL, "Player Uninvited: " + name + " [Uninvited by: " + sender.getName() + "]");
                    team.getInvitations().remove(remove);
                    team.flagForSave();
                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + remove + "!");
                } else {
                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + name + "'!");
                }
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

}