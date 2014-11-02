package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Claims {

    @Command(names={ "team claims", "t claims", "f claims", "faction claims", "fac claims" }, permissionNode="")
    public static void teamClaims(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");

		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());
		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}

		boolean other = false;
		if (args.length > 1) {
			other = true;
			team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(args[1]);
			if (team == null) {
				sender.sendMessage(ChatColor.RED + "That team could not be found!");
				return;
			}
		}

		if (team.getClaims().size() == 0) {
			if (other) {
				sender.sendMessage(ChatColor.RED + "That team has no claimed land!");

			} else {
				sender.sendMessage(ChatColor.RED + "Your team has no claimed land!");
			}
		} else {
			sender.sendMessage(ChatColor.GRAY + "-- §3" + team.getFriendlyName() + "'s Claims§7 --");

			for (net.frozenorb.foxtrot.team.claims.Claim cc : team.getClaims()) {
				sender.sendMessage("  §7" + cc.getFriendlyName());
			}
		}

	}

}