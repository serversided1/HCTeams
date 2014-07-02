package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.PhysicalChunk;
import net.frozenorb.foxtrot.team.claims.LandBoard;

public class Unclaim extends Subcommand {

	public Unclaim(String name, String errorMessage, String... aliases) {
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

			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("all")) {
					team.getChunks().clear();
					team.setRally(null, true);
					team.setHQ(null, true);
					LandBoard.getInstance().clear(team);
					sender.sendMessage(ChatColor.RED + "You have unclaimed all of your chunks!");
					return;
				}
			}

			Chunk c = p.getLocation().getChunk();

			int x = c.getX();
			int z = c.getZ();

			PhysicalChunk cc = new PhysicalChunk(x, z);

			if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(cc) && team.getChunks().contains(cc)) {
				team.getChunks().remove(cc);
				team.flagForSave();

				LandBoard.getInstance().setTeamAt(cc, null);

				p.sendMessage(ChatColor.RED + "You have unclaimed the chunk (" + x + ", " + z + ").");

				if (team.getHQ() != null && team.getHQ().getChunk().getX() == c.getX() && team.getHQ().getChunk().getZ() == c.getZ()) {
					team.setHQ(null, true);
					sender.sendMessage(ChatColor.RED + "Your HQ was in this chunk, so it has been unset.");
				}
				if (team.getRally() != null && team.getRally().getChunk().getX() == c.getX() && team.getRally().getChunk().getZ() == c.getZ()) {
					team.setRally(null, true);
					sender.sendMessage(ChatColor.RED + "Your rally was in this chunk, so it has been unset.");
				}
				return;
			}

			p.sendMessage(ChatColor.RED + "You do not own this chunk. To unclaim all chunks, type '§e/t unclaim all§c'.");

		} else
			p.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
