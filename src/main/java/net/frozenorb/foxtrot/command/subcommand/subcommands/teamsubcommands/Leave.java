package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.PhysicalChunk;
import net.frozenorb.foxtrot.team.claims.LandBoard;

public class Leave extends Subcommand {

	public Leave(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;
		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

		if (team == null) {
			p.sendMessage(ChatColor.GRAY + "You are not on a team!");
		} else {

			if (team.isOwner(p.getName()) && team.getMemberAmount() > 1) {

				p.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
				return;
			}

			Chunk c = p.getLocation().getChunk();
			PhysicalChunk cc = new PhysicalChunk(c.getX(), c.getZ());

			if (FoxtrotPlugin.getInstance().getTeamManager().getOwner(cc) == team) {
				sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
				return;
			}

			p.removeMetadata("teamChat", FoxtrotPlugin.getInstance());

			if (team.remove(sender.getName())) {
				FoxtrotPlugin.getInstance().getTeamManager().removePlayerFromTeam(sender.getName());
				FoxtrotPlugin.getInstance().getTeamManager().removeTeam(team.getName());
				p.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");

				LandBoard.getInstance().clear(team);
			} else {
				FoxtrotPlugin.getInstance().getTeamManager().removePlayerFromTeam(sender.getName());
				team.setChanged(true);
				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (team.isOnTeam(pl)) {
						pl.sendMessage(ChatColor.DARK_AQUA + p.getName() + " has left the team.");
					}
				}
				p.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
			}

			NametagManager.reloadPlayer(p);
			NametagManager.sendTeamsToPlayer(p);
		}

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}
}
