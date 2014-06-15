package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

public class HQ extends Subcommand {

	public HQ(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;
		if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()) == null) {
			p.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
			return;

		}
		final Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
		if (team.getHQ() == null) {
			sender.sendMessage(ChatColor.RED + "HQ not set.");
			return;
		}
		if (p.getWorld().getEnvironment() == Environment.THE_END) {
			p.sendMessage(ChatColor.RED + "You can only exit the End through the End Portal!");
			return;
		}

		FoxtrotPlugin.getInstance().getServerManager().beginWarp(p, team.getHQ(), 15);

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
