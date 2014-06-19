package net.frozenorb.foxtrot.command.commands;

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
}
