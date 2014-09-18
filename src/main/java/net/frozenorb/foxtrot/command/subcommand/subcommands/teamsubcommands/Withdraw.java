package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.mBasic.Basic;

public class Withdraw extends Subcommand {

	public Withdraw(String name, String errorMessage, String... aliases) {
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

			if (team.isCaptain(p.getName()) || team.isOwner(p.getName())) {
				try {
					double deposit = Double.parseDouble(args[1]);

					if (team.getBalance() < deposit) {
						p.sendMessage("§cThe team doesn't have enough money to do this!");
						return;
					}

					Basic.get().getEconomyManager().depositPlayer(p.getName(), deposit);
					p.sendMessage(ChatColor.YELLOW + "You have withdrawn §d" + deposit + "§e from the team balance!");

					team.setBalance(team.getBalance() - deposit);

					team.getOnlineMembers().forEach(pe -> pe.sendMessage("§e" + p.getName() + " withdrew §d" + deposit + " §efrom the team balance."));
				}
				catch (NumberFormatException e) {
					p.sendMessage(ChatColor.RED + "Number couldn't be parsed: " + e.getMessage());
				}
			} else
				p.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
		} else {
			sendErrorMessage();
		}

	}

	@Override
	public List<String> tabComplete() {
		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(sender.getName());

		if (team != null) {
			return Arrays.asList(new String[] { team.getBalance() + "" });
		} else {
			return Arrays.asList(new String[] {});
		}
	}
}
