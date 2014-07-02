package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.server.ServerManager;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.PhysicalChunk;
import net.frozenorb.foxtrot.team.claims.LandBoard;

public class Claim extends Subcommand {

	private static final BlockFace[] AXIAL = { BlockFace.NORTH, BlockFace.EAST,
			BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH_EAST,
			BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST };

	public Claim(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		if (p.getWorld().getEnvironment() == Environment.NETHER) {
			p.sendMessage(ChatColor.RED + "You cannot claim chunks in the nether.");
			return;
		}

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}

		if (team.isRaidaible()) {

			p.sendMessage(ChatColor.RED + "You cannot claim land if your team is raidable!");
			return;
		}
		if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {

			if (p.getLocation().distance(new Location(p.getWorld(), 0, 0, 0)) <= ServerManager.WARZONE_RADIUS + 16) {
				p.sendMessage(ChatColor.RED + "You cannot claim land this close to the Warzone!");
				return;
			}

			Chunk c = p.getLocation().getChunk();
			int x = c.getX();
			int z = c.getZ();

			if (team.getChunks().size() >= team.getMaxChunkAmount()) {
				p.sendMessage(ChatColor.RED + "You have claimed the maximum amount of chunks your team can.");
				return;
			}
			PhysicalChunk cc = new PhysicalChunk(x, z);

			if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(cc)) {
				Team cl = FoxtrotPlugin.getInstance().getTeamManager().getOwner(cc);
				if (cl == team) {
					p.sendMessage(ChatColor.RED + "Your team already claimed this chunk.");
				} else {
					p.sendMessage(ChatColor.YELLOW + "This chunk is owned by §c" + cl.getFriendlyName() + "§e.");
				}
				return;
			}

			if (team.getChunks().size() > 0) {
				boolean touching = false;

				for (PhysicalChunk hasClaimed : team.getChunks()) {
					int diffX = Math.abs(hasClaimed.getX()) - Math.abs(cc.getX());
					int diffZ = Math.abs(hasClaimed.getZ()) - Math.abs(cc.getZ());

					int actualDiff = Math.abs(diffX) + Math.abs(diffZ);

					if (actualDiff == 1) {
						touching = true;
					}
				}
				if (!touching) {
					p.sendMessage(ChatColor.RED + "You can only claim chunks adjacent to your existing claims.");
					return;
				}
			}

			for (BlockFace bf : AXIAL) {
				int i = bf.getModX();
				int j = bf.getModZ();

				PhysicalChunk c22 = new PhysicalChunk(x + i, z + j);

				if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(c22) && !team.getChunks().contains(c22)) {
					p.sendMessage(ChatColor.RED + "Failed! §eThat chunk is adjacent to a chunk claimed by §3" + FoxtrotPlugin.getInstance().getTeamManager().getOwner(c22).getFriendlyName() + "§e.");
					return;
				}

			}

			team.getChunks().add(cc);
			team.flagForSave();

			LandBoard.getInstance().setTeamAt(cc, team);

			sender.sendMessage(ChatColor.GRAY + "Your team has claimed the chunk at (" + x + ", " + z + ").");
		} else
			p.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
