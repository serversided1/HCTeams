package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

public class Demote extends Subcommand {

	public Demote(String name, String errorMessage, String... aliases) {
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
			if (team.isOwner(p.getName()) || p.isOp()) {
				if (team.isMember(name)) {

					if (!team.isCaptain(name)) {
						p.sendMessage(ChatColor.RED + "You can only demote team Captains!");
						return;
					}

					for (Player pm : team.getOnlineMembers()) {
						pm.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(name) + " has been made a member!");
					}

					team.removeCaptain(name);

				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
				}
			} else
				p.sendMessage(ChatColor.DARK_AQUA + "Only team leaders can do this.");
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

		return Arrays.asList(team.getCaptains().toArray(new String[] {}));
	}
}
