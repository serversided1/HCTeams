package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NameClaim {

    @Command(names={ "team nameclaim", "t nameclaim", "f nameclaim", "faction nameclaim", "fac nameclaim" }, permissionNode="")
    public static void teamNameClaim(Player sender, @Param(name="Parameter") String params) {
        String[] args = ("arg1 " + params).split(" ");
		final Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());
		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}
		if (team.isOwner(p.getName())) {

			if (args.length > 1) {

				if (FoxtrotPlugin.getInstance().getTeamHandler().isTaken(p.getLocation()) && team.ownsLocation(p.getLocation())) {

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

}