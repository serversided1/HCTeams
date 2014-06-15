package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.ClaimedChunk;
import net.frozenorb.foxtrot.team.Team;

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
		if (p.getWorld().getEnvironment() == Environment.THE_END) {
			p.sendMessage(ChatColor.RED + "You can only exit the End through the End Portal!");
			return;
		}
		org.bukkit.Chunk h = team.getRally().getChunk();
		ClaimedChunk pCC = new ClaimedChunk(p.getLocation().getChunk().getX(), p.getLocation().getChunk().getZ());

		if (FoxtrotPlugin.getInstance().getTeamManager().getOwner(new ClaimedChunk(h.getX(), h.getZ())) != team) {
			if (FoxtrotPlugin.getInstance().getTeamManager().getOwner(pCC) == team) {
				sender.sendMessage(ChatColor.RED + "You can only warp to your rally from outside of your claimed land!");
				return;
			}
		}
		FoxtrotPlugin.getInstance().getServerManager().beginWarp(p, team.getRally(), 30);

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
