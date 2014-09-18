package net.frozenorb.foxtrot.command.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.team.Team;

public class ForceLeader extends BaseCommand {

	public ForceLeader() {
		super("forceleader");
	}

	@Override
	public void syncExecute() {
		if (sender.hasPermission("foxtrot.forceleader")) {
			if (args.length > 1) {
				String teamName = args[0];
				String playerName = args[1];

				Team team = FoxtrotPlugin.getInstance().getTeamManager().getTeam(teamName);

				if (team == null) {
					sender.sendMessage(ChatColor.RED + "No team by the name of '" + teamName + "' could be found!");
					return;
				}

				if (!FoxtrotPlugin.getInstance().getPlaytimeMap().contains(playerName)) {
					sender.sendMessage(ChatColor.RED + "That player has never played here before!");
					return;
				} else {
					if (FoxtrotPlugin.getInstance().getTeamManager().isOnTeam(playerName) && FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(playerName).isOwner(playerName)) {
						sender.sendMessage(ChatColor.RED + "That player is the owner of their current team!");
						return;
					}
					FoxtrotPlugin.getInstance().getTeamManager().removePlayerFromTeam(playerName);

					FoxtrotPlugin.getInstance().getTeamManager().setTeam(playerName, team);
					team.addMember(playerName);
					team.setOwner(playerName);

					Player p = Bukkit.getPlayerExact(playerName);
					if (p != null) {
						p.sendMessage(ChatColor.YELLOW + "You are now the owner of §b" + team.getFriendlyName());

					}

					sender.sendMessage(ChatColor.YELLOW + playerName + " is now the owner of §b" + team.getFriendlyName());
				}

			} else {
				sender.sendMessage(ChatColor.RED + "/forceleader <team> <player>");
			}
		}
	}

	@Override
	public List<String> getTabCompletions() {
		if (args.length == 1) {
			return FoxtrotPlugin.getInstance().getTeamManager().getTeams().stream().map(t -> t.getFriendlyName()).collect(Collectors.toList());

		}
		return null;
	}

}
