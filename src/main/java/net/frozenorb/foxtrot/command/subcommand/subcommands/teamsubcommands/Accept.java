package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Accept extends Subcommand {

	public Accept(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;
		TeamManager teamManager = FoxtrotPlugin.getInstance().getTeamManager();
		if (args.length > 1) {
			if (teamManager.teamExists(args[1])) {
				Team team = teamManager.getTeam(args[1]);

				if (team.getInvitations().contains(p.getName())) {
					if (FoxtrotPlugin.getInstance().getTeamManager().isOnTeam(p.getName())) {
						sender.sendMessage(ChatColor.RED + "You are already on a team!");
						return;
					}

					team.getInvitations().remove(p.getName());
					team.addMember(p.getName());
					FoxtrotPlugin.getInstance().getTeamManager().setTeam(p.getName(), team);

					for (Player ps : Bukkit.getOnlinePlayers()) {
						if (team.isOnTeam(ps)) {
							ps.sendMessage(ChatColor.GRAY + p.getName() + " has joined the team!");
						}
					}

				} else {
					sender.sendMessage(ChatColor.RED + "This team has not invited you!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "No such team could be found!");
			}
		} else
			sender.sendMessage(ChatColor.RED + "/t accept <teamName>");
	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
