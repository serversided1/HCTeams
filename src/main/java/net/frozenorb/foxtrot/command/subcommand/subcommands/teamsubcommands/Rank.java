package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Rank extends Subcommand {

	public Rank(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		if (args.length > 2) {

			String name = args[1];
			String rankName = args[2];

			if (Bukkit.getPlayer(args[1]) != null) {
				name = Bukkit.getPlayer(args[1]).getName();
			}
			Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(sender.getName());

			if (team == null) {
				sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
				return;
			}

			if (team.isOwner(sender.getName())) {

				if (team.isOnTeam(name)) {

					if (team.isOwner(name)) {
						sender.sendMessage(ChatColor.RED + "You cannot use this on the team leader!");
						return;
					}

					if (rankName.equalsIgnoreCase("captain")) {

						if (!team.isCaptain(name)) {
							team.addCaptain(name);

							for (Player pm : team.getOnlineMembers()) {
								pm.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(name) + " has been made a Captain!");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "That player is already a Captain!");
						}

					} else if (rankName.equalsIgnoreCase("member")) {
						if (team.isCaptain(name)) {
							team.removeCaptain(name);

							for (Player pm : team.getOnlineMembers()) {
								pm.sendMessage(ChatColor.DARK_AQUA + team.getActualPlayerName(name) + " has been made a member!");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "That player is already a member!");
						}

					} else if (rankName.equalsIgnoreCase("leader")) {
						sender.sendMessage(ChatColor.RED + "Use '§e/t newleader <name>§c' to choose a new leader.");
					}

				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
				}

			} else
				sender.sendMessage(ChatColor.DARK_AQUA + "Only team leaders can do this.");

		} else {
			sendErrorMessage();
		}

	}

	@Override
	public List<String> tabComplete() {

		LinkedList<String> params = new LinkedList<String>(Arrays.asList(args));
		LinkedList<String> results = new LinkedList<String>();

		if (params.size() >= 1) {
			params.pop().toLowerCase();
		} else {
			return results;

		}

		if (params.size() == 2) {

			return Arrays.asList(new String[] { "~SECOND_STRING", "captain",
					"member" });

		}
		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(sender.getName());
		if (team == null) {
			return super.tabComplete();
		}
		return Arrays.asList(team.getMembers().toArray(new String[] {}));
	}
}
