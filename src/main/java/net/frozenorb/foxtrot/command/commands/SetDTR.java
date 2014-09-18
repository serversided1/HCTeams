package net.frozenorb.foxtrot.command.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.team.Team;

public class SetDTR extends BaseCommand {

	public SetDTR() {
		super("setdtr");
		setPermissionLevel("foxtrot.setdtr", "§cYou are not allowed to do this, ya silly goose!");
	}

	@Override
	public void syncExecute() {
		if (args.length == 2) {
			String teamName = args[0];

			Team t = FoxtrotPlugin.getInstance().getTeamManager().getTeam(teamName);

			if (t == null) {
				sender.sendMessage(ChatColor.RED + "That team doesn't exist!");
				return;
			}

			try {
				double dtr = Double.parseDouble(args[1]);
				t.setDtr(dtr);
				sender.sendMessage(ChatColor.YELLOW + t.getName() + " has a new DTR of: " + dtr);
			}
			catch (NumberFormatException ex) {
				sender.sendMessage(ex.getMessage());
				return;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "/setdtr <team> <dtr>");
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
