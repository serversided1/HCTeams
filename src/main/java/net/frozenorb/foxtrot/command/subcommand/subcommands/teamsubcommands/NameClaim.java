package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;

public class NameClaim extends Subcommand {

	public NameClaim(String name, String errorMessage, String... aliases) {
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

				if (FoxtrotPlugin.getInstance().getTeamManager().isTaken(p.getLocation()) && team.ownsLocation(p.getLocation())) {

					net.frozenorb.foxtrot.team.claims.Claim cc = LandBoard.getInstance().getClaimAt(p.getLocation());

					cc.setName(args[1]);
					p.sendMessage(ChatColor.YELLOW + "You have renamed this claim to: §f" + args[1]);
					return;
				}

				p.sendMessage(ChatColor.RED + "You do not own this claims. To unclaim all claims, type '§e/t unclaim all§c'.");
			} else {
				p.sendMessage(ChatColor.RED + "/t nameclaim <name>");
			}
		} else
			p.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<String>();
	}

}
