package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Forcejoin extends Subcommand {

	public Forcejoin(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;

		if (!p.hasPermission("foxtrot.forcejoin")) {
			p.sendMessage(ChatColor.RED + "You are not allowed to do this.");
			return;
		}

		TeamManager teamManager = FoxtrotPlugin.getInstance().getTeamManager();
		if (args.length > 1) {
			if (teamManager.teamExists(args[1])) {
				Team team = teamManager.getTeam(args[1]);

				if (teamManager.isOnTeam(p.getName())) {
					sender.sendMessage(ChatColor.RED + "Leave your current team before attempting to forcejoin.");
					return;
				}

				team.addMember(p.getName());
				teamManager.setTeam(p.getName(), team);
				p.sendMessage(ChatColor.GREEN + "You are now a member of §b" + team.getFriendlyName() + "§a!");
			}
		} else
			sender.sendMessage(ChatColor.RED + "/t forcejoin <tName>");
	}

	@Override
	public List<String> tabComplete() {
		ArrayList<String> teamNames = new ArrayList<String>();
		for (Team tem : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {
			teamNames.add(tem.getFriendlyName());
		}
		return teamNames;
	}
}
