package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamNameClaimCommand {

    @Command(names={ "team nameclaim", "t nameclaim", "f nameclaim", "faction nameclaim", "fac nameclaim", "team renameclaim", "t renameclaim", "f renameclaim", "faction renameclaim", "fac renameclaim" }, permissionNode="")
    public static void teamNameClaim(Player sender, @Param(name="name", wildcard=true) String name) {
		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}

        if (!sender.isOp()) {
            name = name.split(" ")[0];
        }

		if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            if (FoxtrotPlugin.getInstance().getTeamHandler().isTaken(sender.getLocation()) && team.ownsLocation(sender.getLocation())) {
                net.frozenorb.foxtrot.team.claims.Claim cc = LandBoard.getInstance().getClaimAt(sender.getLocation());

                cc.setName(name);
                sender.sendMessage(ChatColor.YELLOW + "You have renamed this claim to: " + ChatColor.WHITE + name);
                return;
            }

            sender.sendMessage(ChatColor.RED + "You do not own this land.");
		} else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team captains can do this.");
        }
	}

}