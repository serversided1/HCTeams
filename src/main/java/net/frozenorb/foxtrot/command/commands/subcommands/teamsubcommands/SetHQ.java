package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public class SetHQ {

    @Command(names={ "team sethq", "t sethq", "f sethq", "faction sethq", "fac sethq", "team sethome", "t sethome", "f sethome", "faction sethome", "fac sethome", "sethome" }, permissionNode="")
    public static void teamInvite(Player sender) {
		Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

		if (team != null) {
			if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {

				if (FoxtrotPlugin.getInstance().getTeamHandler().getOwner(p.getLocation()) != team) {
					sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
					return;
				}
				Location loc = ((Player) sender).getLocation();
				if (loc.getWorld().getEnvironment() == Environment.THE_END) {
					sender.sendMessage(ChatColor.RED + "You cannot set warps in the end!");
					return;
				}
				team.setHQ(p.getLocation());

				for (Player pl : Bukkit.getOnlinePlayers()) {
					if (team.isMember(pl)) {
						pl.sendMessage(ChatColor.ITALIC + "" + ChatColor.DARK_AQUA + p.getName() + " has updated the team's HQ point!");
					}
				}

				p.sendMessage(ChatColor.DARK_AQUA + "Headquarters Set");
				return;

			} else if (team.isMember(p)) {
				sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
				return;
			}
		} else {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
		}

	}

}