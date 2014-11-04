package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Unclaim {

    @Command(names={ "team unclaim", "t unclaim", "f unclaim", "faction unclaim", "fac unclaim" }, permissionNode="")
    public static void teamUnclaim(Player sender, @Param(name="all?", defaultValue="f") String all) {
		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}

		if (team.isOwner(sender.getName())) {
            if (team.isRaidable()) {
                sender.sendMessage(ChatColor.RED + "You may not unclaim land while your faction is raidable!");
                return;
            }

			if (all.equalsIgnoreCase("all")) {
                int claims = team.getClaims().size();
                double refund = 0.0D;

                for (net.frozenorb.foxtrot.team.claims.Claim claim : team.getClaims()) {
                    refund += net.frozenorb.foxtrot.team.claims.Claim.getPrice(claim, team, false);
                }

                team.setBalance(team.getBalance() + refund);
                team.getClaims().clear();
                team.setHQ(null);
                LandBoard.getInstance().clear(team);
                sender.sendMessage(ChatColor.RED + "You have unclaimed all of your claims (" + claims + " total)! Your team was refunded $" + refund + ".");
                return;
			}

			if (FoxtrotPlugin.getInstance().getTeamHandler().isTaken(sender.getLocation()) && team.ownsLocation(sender.getLocation())) {
				net.frozenorb.foxtrot.team.claims.Claim cc = LandBoard.getInstance().getClaimAt(sender.getLocation());
                int refund = net.frozenorb.foxtrot.team.claims.Claim.getPrice(cc, team, false);

                team.setBalance(team.getBalance() + refund);
				team.getClaims().remove(cc);
				team.flagForSave();

				LandBoard.getInstance().setTeamAt(cc, null);

				sender.sendMessage(ChatColor.RED + "You have unclaimed the claim §d" + cc.getFriendlyName() + "§c! Your team was refunded §d$" + refund + "§c!");

				if (team.getHq() != null && cc.contains(team.getHq())) {
					team.setHQ(null);
					sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
				}

				return;
			}

			sender.sendMessage(ChatColor.RED + "You do not own this claim. To unclaim all claims, type '§e/t unclaim all§c'.");
		} else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
        }
	}

}
