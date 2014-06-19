package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.ClaimedChunk;
import net.frozenorb.foxtrot.team.Team;

public class SetRally extends Subcommand {

	public SetRally(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;

		if (p.getWorld().getEnvironment() == Environment.NETHER) {
			p.sendMessage(ChatColor.RED + "You cannot set rally in the nether.");
			return;
		}

		final Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

		if (team != null) {
			if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {
				org.bukkit.Chunk h = p.getLocation().getChunk();

				if (FoxtrotPlugin.getInstance().getServerManager().isWarzone(p.getLocation()) || FoxtrotPlugin.getInstance().getTeamManager().isTaken(new ClaimedChunk(h.getX(), h.getZ()))) {
					sender.sendMessage(ChatColor.RED + "You can only set rally in unclaimed territory!");
					return;
				}
				Location loc = ((Player) sender).getLocation();
				if (loc.getWorld().getEnvironment() == Environment.THE_END) {
					sender.sendMessage(ChatColor.RED + "You cannot set warps in the end!");
					return;
				}
				team.setRally(p.getLocation(), true);
				if (team.getRunnable() != null && Bukkit.getScheduler().isCurrentlyRunning(team.getRunnable().getTaskId())) {
					team.getRunnable().cancel();
				}

				team.setRunnable(new BukkitRunnable() {

					@Override
					public void run() {
						team.setRally(null, true);
						for (Player p : team.getOnlineMembers()) {
							p.sendMessage(ChatColor.DARK_AQUA + "Your team's rally point has expired!");
						}
					}
				});
				team.getRunnable().runTaskLater(FoxtrotPlugin.getInstance(), 5 * 20L * 60L);
				team.setRallyExpires(System.currentTimeMillis() + 5 * 60 * 1000L);

				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (team.isOnTeam(pl)) {
						pl.sendMessage(ChatColor.ITALIC + "" + ChatColor.DARK_AQUA + p.getName() + " has updated the team's rally point!");
					}
				}

				p.sendMessage(ChatColor.DARK_AQUA + "Rally Set");
				return;

			} else if (team.isOnTeam(p)) {
				sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
				return;
			}
		} else {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
		}

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
