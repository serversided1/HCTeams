package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class Kick {

    @Command(names={ "team kick", "t kick", "f kick", "faction kick", "fac kick" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
		final Player p = (Player) sender;

		if (args.length == 2) {

			String name = args[1];
			if (Bukkit.getPlayer(args[1]) != null) {
				name = Bukkit.getPlayer(args[1]).getName();
			}
			Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());
			if (team == null) {
				sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
				return;
			}
			if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {
				if (team.isMember(name)) {

					if (team.isOwner(name)) {
						sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
						return;
					}

					if (team.isOwner(name) || team.isCaptain(name)) {
						if (team.isCaptain(p.getName())) {
							p.sendMessage(ChatColor.RED + "Only the leader can kick other captains!");
							return;
						}
					}

					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (team.isMember(pl)) {
							pl.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(name) + " was kicked by " + p.getName() + "!");
						}
					}

					if (team.removeMember(name))
						FoxtrotPlugin.getInstance().getTeamHandler().removeTeam(team.getName());

					FoxtrotPlugin.getInstance().getTeamHandler().removePlayerFromTeam(name);

					if (Bukkit.getPlayerExact(name) != null) {
						NametagManager.reloadPlayer(Bukkit.getPlayerExact(name));
						NametagManager.sendTeamsToPlayer(Bukkit.getPlayerExact(name));
					}

				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
				}
			} else
				p.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
		} else {

		}

	}

}