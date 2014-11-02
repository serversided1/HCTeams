package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Unclaim extends Subcommand {

	public Unclaim(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}
		if (team.isOwner(p.getName())) {
            if(team.isRaidable()){
                p.sendMessage(ChatColor.RED + "You may not unclaim land while your faction is raidable!");
                return;
            }

			if(args.length > 1){
				if(args[1].equalsIgnoreCase("all")){
                    int claims = team.getClaims().size();
                    double refund = 0.0D;

                    for(net.frozenorb.foxtrot.team.claims.Claim claim : team.getClaims()){
                        refund += net.frozenorb.foxtrot.team.claims.Claim.getPrice(claim, team, false);
                    }

                    team.setBalance(team.getBalance() + refund);
					team.getClaims().clear();
					team.setHQ(null);
					LandBoard.getInstance().clear(team);
					sender.sendMessage(ChatColor.RED + "You have unclaimed all of your claims (" + claims + " total)! Your team was refunded $" + refund + ".");
					return;
				}
			}

			if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(p.getLocation()) && team.ownsLocation(p.getLocation())) {

				net.frozenorb.foxtrot.team.claims.Claim cc = LandBoard.getInstance().getClaimAt(p.getLocation());
                int refund = net.frozenorb.foxtrot.team.claims.Claim.getPrice(cc, team, false);

                team.setBalance(team.getBalance() + refund);
				team.getClaims().remove(cc);
				team.flagForSave();

				LandBoard.getInstance().setTeamAt(cc, null);

				p.sendMessage(ChatColor.RED + "You have unclaimed the claim §d" + cc.getFriendlyName() + "§c! Your team was refunded §d$" + refund + "§c!");

				if (team.getHq() != null && cc.contains(team.getHq())) {
					team.setHQ(null);
					sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
				}

				return;
			}

			p.sendMessage(ChatColor.RED + "You do not own this claim. To unclaim all claims, type '§e/t unclaim all§c'.");

		} else
			p.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
