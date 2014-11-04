package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.mBasic.Basic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Withdraw {

    @Command(names={ "team withdraw", "t withdraw", "f withdraw", "faction withdraw", "fac withdraw", "team w", "t w", "f w", "faction w", "fac w" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
		final Player p = (Player) sender;

		if (args.length > 1) {

			Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

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

		}

	}

}