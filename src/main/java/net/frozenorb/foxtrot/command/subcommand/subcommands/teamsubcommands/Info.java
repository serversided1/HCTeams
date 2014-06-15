package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;

public class Info extends Subcommand {

	public Info(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		Player p = (Player) sender;
		if (args.length > 1) {
			Player target = Bukkit.getServer().getPlayer(args[1]);

			if (target == null || !((Player) sender).canSee(target)) {
				String n = args[1];
				if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(n) == null) {
					p.sendMessage(ChatColor.GRAY + "That player is not on a team!");
				} else {
					net.frozenorb.foxtrot.team.Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(n);

					team.sendTeamInfo(p);

				}
			} else {
				if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(target.getName()) == null) {
					p.sendMessage(ChatColor.GRAY + "That player is online, but not on a team!");
				} else {
					net.frozenorb.foxtrot.team.Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(target.getName());

					team.sendTeamInfo(p);

				}
			}
		} else {
			if (FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()) == null)
				p.sendMessage(ChatColor.GRAY + "You are not on a team!");
			else {
				net.frozenorb.foxtrot.team.Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

				team.sendTeamInfo(p);

			}
		}
	}
}
