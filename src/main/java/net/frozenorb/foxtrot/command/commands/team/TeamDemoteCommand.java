package net.frozenorb.foxtrot.command.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class TeamDemoteCommand {

    @Command(names={ "team demote", "t demote", "f demote", "faction demote", "fac demote" }, permissionNode="")
    public static void teamDemote(Player sender, @Param(name="player") OfflinePlayer name) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getName()) || sender.isOp()) {
            if (team.isMember(name.getName())) {
                if (!team.isCaptain(name.getName())) {
                    sender.sendMessage(ChatColor.RED + "You can only demote team Captains!");
                    return;
                }

                for (Player player : team.getOnlineMembers()) {
                    player.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(name.getName()) + " has been removed as an officer!");
                }

                team.removeCaptain(name.getName());
            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team leaders can do this.");
        }
    }

}