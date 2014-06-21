package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;

@SuppressWarnings("deprecation")
public class Kick extends Subcommand {

	public Kick(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		if (args.length == 2) {

			String name = args[1];
			if (Bukkit.getPlayer(args[1]) != null) {
				name = Bukkit.getPlayer(args[1]).getName();
			}
			Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
			if (team == null) {
				sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
				return;
			}
			if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {
				if (team.isOnTeam(name)) {

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
						if (team.isOnTeam(pl)) {
							pl.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(name) + " was kicked by " + p.getName() + "!");
						}
					}

					if (team.remove(name))
						FoxtrotPlugin.getInstance().getTeamManager().removeTeam(team.getName());

					FoxtrotPlugin.getInstance().getTeamManager().removePlayerFromTeam(name);

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
			sendErrorMessage();
		}

	}

	@Override
	public List<String> tabComplete() {
		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(sender.getName());
		if (team == null) {
			return super.tabComplete();
		}

		return Arrays.asList(team.getMembers().toArray(new String[] {}));
	}
}
