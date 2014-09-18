package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import org.bukkit.ChatColor;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

public class Rename extends Subcommand {

	public Rename(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		if (sender.hasPermission("foxtrot.rename")) {
			if (args.length == 3) {
				String teamName = args[1];
				String newName = args[2];

				Team existing = FoxtrotPlugin.getInstance().getTeamManager().getTeam(teamName);

				if (existing == null) {
					sender.sendMessage(ChatColor.RED + "No team with the name '" + teamName + "' could be found!");
					return;
				}

				if (FoxtrotPlugin.getInstance().getTeamManager().getTeam(newName) != null) {
					sender.sendMessage(ChatColor.RED + "A team with the name '" + newName + "' exists!");
					return;
				}

				FoxtrotPlugin.getInstance().getTeamManager().renameTeam(existing, newName);
				sender.sendMessage(ChatColor.RED + "Team renamed to " + newName);
			} else {
				sender.sendMessage(ChatColor.RED + "/t rename <team> <newname>");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You are not allowed to do this!");
		}
	}

}
