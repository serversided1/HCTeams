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
public class Leave {

    @Command(names={ "team leave", "t leave", "f leave", "faction leave", "fac leave" }, permissionNode="")
    public static void teamCreate(Player sender) {
		final Player p = (Player) sender;
		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

		if (team == null) {
			p.sendMessage(ChatColor.GRAY + "You are not on a team!");
		} else {

			if (team.isOwner(p.getName()) && team.getSize() > 1) {

				p.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
				return;
			}

			if (FoxtrotPlugin.getInstance().getTeamHandler().getOwner(p.getLocation()) == team) {
				sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
				return;
			}

			p.removeMetadata("teamChat", FoxtrotPlugin.getInstance());

			if (team.removeMember(sender.getName())) {
				FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(sender.getName());
				FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(team.getName());
				p.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");

				LandBoard.getInstance().clear(team);

			} else {
				FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(sender.getName());

				team.setChanged(true);
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (team.isMember(pl)) {
						pl.sendMessage(ChatColor.YELLOW + p.getName() + " has left the team.");
					}
				}
				p.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
			}

			NametagManager.reloadPlayer(p);
			NametagManager.sendTeamsToPlayer(p);
		}

	}

}