package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.mBasic.Basic;

public class Deposit extends Subcommand {

	public Deposit(String name, String errorMessage, String... aliases) {
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

			if (!FoxtrotPlugin.getInstance().getServerManager().isSpawn(p.getLocation()) && !FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()).ownsLocation(p.getLocation())) {
				sender.sendMessage(ChatColor.RED + "You can only do this in spawn or in your own territory!");
				return;
			}

			boolean override = false;

			if (sender.hasPermission("foxtrot.override.team.balance") && args.length > 2) {
				String teamName = args[2];
				override = true;

				if (FoxtrotPlugin.getInstance().getTeamManager().getTeam(teamName) == null) {
					p.sendMessage(ChatColor.RED + "No such team could be found!");
					return;
				} else {
					team = FoxtrotPlugin.getInstance().getTeamManager().getTeam(teamName);
					override = true;
				}

			}

			try {
				double deposit = Double.parseDouble(args[1]);

                if(deposit == 0){
                    p.sendMessage(ChatColor.RED + "You can't deposit $0.0!");
                    return;
                }

                if(deposit < 0){
                    p.sendMessage(ChatColor.RED + "Deposit value must be more than 0!");
                    return;
                }

				if (Basic.get().getEconomyManager().getBalance(p.getName()) < deposit && !override) {
					p.sendMessage("§cYou don't have enough money to do this!");
					return;
				}

				if (!override) {
					Basic.get().getEconomyManager().withdrawPlayer(p.getName(), deposit);
				}

				p.sendMessage(ChatColor.YELLOW + "You have added §d" + deposit + "§e to the team balance!");
				team.setBalance(team.getBalance() + deposit);

				if (!override) {
					team.getOnlineMembers().forEach(pe -> pe.sendMessage("§e" + p.getName() + " deposited §d" + deposit + " §einto the team balance."));
				}

			}
			catch (NumberFormatException e) {
				p.sendMessage(ChatColor.RED + "Number couldn't be parsed: " + e.getMessage());
			}

		} else {
			sendErrorMessage();
		}

	}

	@Override
	public List<String> tabComplete() {
		return Arrays.asList(Basic.get().getEconomyManager().getBalance(sender.getName()) + "");
	}
}
