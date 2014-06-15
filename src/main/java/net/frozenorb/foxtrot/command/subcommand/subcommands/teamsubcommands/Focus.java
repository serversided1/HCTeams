package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Focus extends Subcommand {

	public Focus(String name, String errorMessage, String... aliases) {
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
		if (team.isOwner(p.getName())) {

		} else
			p.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
