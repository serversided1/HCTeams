package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;

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

			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("all")) {
					team.getClaims().clear();
					team.setRally(null, true);
					team.setHQ(null, true);
					LandBoard.getInstance().clear(team);
					sender.sendMessage(ChatColor.RED + "You have unclaimed all of your claims!");
					return;
				}
			}

			if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(p.getLocation()) && team.ownsLocation(p.getLocation())) {

				net.frozenorb.foxtrot.team.claims.Claim cc = LandBoard.getInstance().getClaimAt(p.getLocation());

				team.getClaims().remove(cc);
				team.flagForSave();

				LandBoard.getInstance().setTeamAt(cc, null);

				p.sendMessage(ChatColor.RED + "You have unclaimed the claim §d" + cc.getFriendlyName() + "§c!");

				if (team.getHQ() != null && cc.contains(team.getHQ())) {
					team.setHQ(null, true);
					sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
				}
				if (team.getRally() != null && cc.contains(team.getRally())) {
					team.setRally(null, true);
					sender.sendMessage(ChatColor.RED + "Your rally was in this claim, so it has been unset.");
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
