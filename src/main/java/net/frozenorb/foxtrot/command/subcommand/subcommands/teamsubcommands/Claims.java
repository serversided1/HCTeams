package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

public class Claims extends Subcommand {

	public Claims(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}

		boolean other = false;
		if (args.length > 1) {
			other = true;
			team = FoxtrotPlugin.getInstance().getTeamManager().getTeam(args[1]);
			if (team == null) {
				sender.sendMessage(ChatColor.RED + "That team could not be found!");
				return;
			}
		}

		if (team.getClaims().size() == 0) {
			if (other) {
				sender.sendMessage(ChatColor.RED + "That team has no claimed land!");

			} else {
				sender.sendMessage(ChatColor.RED + "Your team has no claimed land!");
			}
		} else {
			sender.sendMessage(ChatColor.GRAY + "-- §3" + team.getFriendlyName() + "'s Claims§7 --");

			for (net.frozenorb.foxtrot.team.claims.Claim cc : team.getClaims()) {
				sender.sendMessage("  §7" + cc.getFriendlyName());
			}
		}

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
