package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class TeamLeaveCommand {

    @Command(names={ "team leave", "t leave", "f leave", "faction leave", "fac leave" }, permissionNode="")
    public static void teamLeave(Player sender) {
		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
		}

        if (team.isOwner(sender.getName()) && team.getSize() >= 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (FoxtrotPlugin.getInstance().getTeamHandler().getOwner(sender.getLocation()) == team) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
            return;
        }

        sender.removeMetadata("teamChat", FoxtrotPlugin.getInstance());

        if (team.removeMember(sender.getName())) {
            FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(sender.getName());
            FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(team.getName());
            LandBoard.getInstance().clear(team);

            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(sender.getName());

            team.setChanged(true);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (team.isMember(player)) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");
                }
            }

            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
        }

        NametagManager.reloadPlayer(sender);
        NametagManager.sendTeamsToPlayer(sender);
	}

}