package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class Create extends Subcommand {

	public Create(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;
		if (args.length == 2) {
			if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()) == null) {
				if (!StringUtils.isAlpha(args[1])) {
					p.sendMessage(ChatColor.GRAY + "Team names can only be alphabetical.");
					return;
				}
				String name = args[1];
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				if (name.length() > 16) {
					p.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
					return;

				}
				if (name.length() < 3) {
					p.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");

					return;
				}

				if (!FoxtrotPlugin.getInstance().getTeamManager().teamExists(name)) {
					net.frozenorb.foxtrot.team.Team team = new net.frozenorb.foxtrot.team.Team(name);
					team.setOwner(p.getName());
					team.setFriendlyFire(false);
					team.setFriendlyName(name);
					FoxtrotPlugin.getInstance().getTeamManager().addTeam(team);
					FoxtrotPlugin.getInstance().getTeamManager().setTeam(p.getName(), team);
					p.sendMessage(ChatColor.DARK_AQUA + "Team Created!");
					p.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

					Bukkit.broadcastMessage("§eTeam §9" + team.getName() + "§e has been §acreated §eby §f" + p.getDisplayName());

				} else {
					p.sendMessage(ChatColor.GRAY + "That team already exists!");
				}
			} else {
				p.sendMessage(ChatColor.GRAY + "You're already in a team!");
			}
			return;
		} else {
			sendErrorMessage();
		}
	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}
}