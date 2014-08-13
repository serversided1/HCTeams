package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamLocationType;
import net.frozenorb.foxtrot.team.claims.PhysicalChunk;

public class Rally extends Subcommand {

	public Rally(String name, String errorMessage, String... aliases) {
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
		if (team.getRally() == null) {
			sender.sendMessage(ChatColor.RED + "Rally not set.");
			return;
		}

		if (team.getRallySetTime() + 30_000 > System.currentTimeMillis()) {
			p.sendMessage(ChatColor.RED + "You cannot use your rally within 30 seconds of it being set!");
			return;
		}

		if (p.getWorld().getEnvironment() == Environment.THE_END) {
			p.sendMessage(ChatColor.RED + "You can only exit the End through the End Portal!");
			return;
		}
		org.bukkit.Chunk h = team.getRally().getChunk();
		PhysicalChunk pCC = new PhysicalChunk(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ());

		if (FoxtrotPlugin.getInstance().getTeamManager().getOwner(new PhysicalChunk(h.getX(), h.getZ())) != team) {
			if (FoxtrotPlugin.getInstance().getTeamManager().getOwner(pCC) == team) {
				sender.sendMessage(ChatColor.RED + "You can only warp to your rally from outside of your claimed land!");
				return;
			}
		}
		FoxtrotPlugin.getInstance().getServerManager().beginWarp(p, team.getRally(), 100, TeamLocationType.RALLY);

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
