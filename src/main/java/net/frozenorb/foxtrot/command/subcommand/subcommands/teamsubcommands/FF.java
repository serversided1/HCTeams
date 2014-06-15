package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;

public class FF extends Subcommand {

	public FF(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		if (args.length > 1) {

			Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

			if (team == null) {
				sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
				return;
			}
			if (args[1].equalsIgnoreCase("on")) {
				if (team.isOwner(p.getName())) {
					team.setFriendlyFire(true);
					p.sendMessage(ChatColor.DARK_AQUA + "You have turned team friendly fire on.");

					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (team.isOnTeam(pl) && pl != p) {
							pl.sendMessage(ChatColor.ITALIC + "" + ChatColor.DARK_AQUA + "Friendly fire was turned on by " + sender.getName() + ".");
						}
					}
				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can change this.");
				}
			} else if (args[1].equalsIgnoreCase("off")) {
				if (team.isOwner(p.getName())) {
					team.setFriendlyFire(false);
					p.sendMessage(ChatColor.DARK_AQUA + "You have turned team friendly fire off.");
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (team.isOnTeam(pl) && pl != p) {
							pl.sendMessage(ChatColor.ITALIC + "" + ChatColor.DARK_AQUA + "Friendly fire was turned off by " + sender.getName() + ".");
						}
					}

				} else {
					p.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can change this.");
				}
			} else
				sender.sendMessage(ChatColor.DARK_AQUA + "/team ff on|off");

		} else {
			sendErrorMessage();
		}

	}

	@Override
	public List<String> tabComplete() {
		return Arrays.asList(new String[] { "on", "off" });
	}

}
