package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

@SuppressWarnings("deprecation")
public class SetHQ extends Subcommand {

	public SetHQ(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

		if (team != null) {
			if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {

				if (FoxtrotPlugin.getInstance().getTeamManager().getOwner(p.getLocation()) != team) {
					sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
					return;
				}
				Location loc = ((Player) sender).getLocation();
				if (loc.getWorld().getEnvironment() == Environment.THE_END) {
					sender.sendMessage(ChatColor.RED + "You cannot set warps in the end!");
					return;
				}
				team.setHQ(p.getLocation(), true);

				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (team.isOnTeam(pl)) {
						pl.sendMessage(ChatColor.ITALIC + "" + ChatColor.DARK_AQUA + p.getName() + " has updated the team's HQ point!");
					}
				}

				p.sendMessage(ChatColor.DARK_AQUA + "Headquarters Set");
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
