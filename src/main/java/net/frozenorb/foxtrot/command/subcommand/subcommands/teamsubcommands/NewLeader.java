package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

public class NewLeader extends Subcommand {

	public NewLeader(String name, String errorMessage, String... aliases) {
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
			if (team.isOwner(p.getName())) {
				if (team.isOnTeam(name)) {
					name = team.getActualPlayerName(name);
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (team.isOnTeam(pl)) {
							pl.sendMessage(ChatColor.DARK_AQUA + name + " is now the new leader!");
						}
					}

					team.setOwner(name);

				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Player is not on your team.");
				}
			} else
				p.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
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
