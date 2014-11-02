package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamLocationType;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

public class HQ {

    @Command(names={ "team hq", "t hq", "f hq", "faction hq", "fac hq", "team home", "t home", "f home", "faction home", "fac home", "home" }, permissionNode="")
    public static void teamForceLeave(Player sender) {
        Player p = sender;

		if(FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName()) == null){
			p.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
			return;

		}

		final Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(p.getName());

        if (team.getHq() == null){
			sender.sendMessage(ChatColor.RED + "HQ not set.");
			return;
		}

        if (p.getWorld().getEnvironment() == Environment.THE_END) {
            p.sendMessage(ChatColor.RED + "You can only exit the End through the End Portal!");
            return;
        }

        if (p.getWorld().getEnvironment() == Environment.NETHER) {
            p.sendMessage(ChatColor.RED + "You may not go to faction headquarters from the Nether!");
            return;
        }

        Team inClaim = LandBoard.getInstance().getTeamAt(p.getLocation());

        if (inClaim != null && inClaim.getDtr() == 100D) {
            p.sendMessage(ChatColor.RED + "You may not go to faction headquarters from inside Citadel!");
            return;
        }

        /*
		if(FoxtrotPlugin.getInstance().getServerHandler().getFHomeCooldown().containsKey(p.getName()) && FoxtrotPlugin.getInstance().getServerHandler().getFHomeCooldown().get(p.getName()) > System.currentTimeMillis()) {
			p.sendMessage(ChatColor.RED + "You cannot warp to your team home within 15 minutes of warping to rally!");
			return;
		}
		*/

		FoxtrotPlugin.getInstance().getServerHandler().beginWarp(p, team.getHq(), 75, TeamLocationType.HOME);

	}

}