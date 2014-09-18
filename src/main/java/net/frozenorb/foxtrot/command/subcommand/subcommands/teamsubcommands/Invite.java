package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

public class Invite extends Subcommand {

	public Invite(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		if (args.length == 2) {

			String name = args[1];
			if (Bukkit.getPlayer(args[1]) != null) {
				name = Bukkit.getPlayer(args[1]).getName();
			} else {
				sender.sendMessage(ChatColor.RED + "That player could not be found.");
				return;
			}
			Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
			if (team == null) {
				sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
				return;
			}
			if (team.getMembers().size() >= Team.MAX_TEAM_SIZE) {
				sender.sendMessage(ChatColor.RED + "The max team size is " + Team.MAX_TEAM_SIZE + "!");
				return;
			}
			if (team.isOwner(p.getName()) || team.isCaptain(p.getName())) {
				if (!team.isOnTeam(name)) {

					if (team.getInvitations().contains(name)) {
						sender.sendMessage(ChatColor.RED + "That player has already been invited.");
						return;
					}
					team.getInvitations().add(name);
					Bukkit.getPlayerExact(name).sendMessage(ChatColor.GRAY + "You have been invited to team '§e" + team.getFriendlyName() + "§7'. Type '§3/team accept §e" + team.getFriendlyName() + "§7' to join.");
					sender.sendMessage("§e" + name + " has been invited to the team!");
				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Player is already on your team.");
				}
			} else
				p.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
		} else {
			sendErrorMessage();
		}

	}

	@Override
	@SuppressWarnings("deprecation")
	public List<String> tabComplete() {
		ArrayList<String> pls = new ArrayList<String>();

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.canSee((Player) sender)) {
				pls.add(p.getName());
			}
		}
		return pls;
	}
}
