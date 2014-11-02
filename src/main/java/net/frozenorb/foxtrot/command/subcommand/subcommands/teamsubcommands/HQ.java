package net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.subcommand.Subcommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamLocationType;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HQ extends Subcommand {

	public HQ(String name, String errorMessage, String... aliases) {
		super(name, errorMessage, aliases);
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		if(FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName()) == null){
			p.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
			return;

		}

		final Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());

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
		if(FoxtrotPlugin.getInstance().getServerManager().getFHomeCooldown().containsKey(p.getName()) && FoxtrotPlugin.getInstance().getServerManager().getFHomeCooldown().get(p.getName()) > System.currentTimeMillis()) {
			p.sendMessage(ChatColor.RED + "You cannot warp to your team home within 15 minutes of warping to rally!");
			return;
		}
		*/

		FoxtrotPlugin.getInstance().getServerManager().beginWarp(p, team.getHq(), 75, TeamLocationType.HOME);

	}

	@Override
	public List<String> tabComplete() {
		return new ArrayList<>();
	}

}
