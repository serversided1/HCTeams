package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

public class TeamHQCommand {

    @Command(names={ "team hq", "t hq", "f hq", "faction hq", "fac hq", "team home", "t home", "f home", "faction home", "fac home", "home" }, permissionNode="")
    public static void teamHQ(Player sender) {
		if (FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName()) == null) {
			sender.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
			return;
		}

		Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team.getHq() == null) {
			sender.sendMessage(ChatColor.RED + "HQ not set.");
			return;
		}

        if (sender.getWorld().getEnvironment() == Environment.THE_END) {
            sender.sendMessage(ChatColor.RED + "You can only exit the End through the End Portal!");
            return;
        }

        if (sender.getWorld().getEnvironment() == Environment.NETHER) {
            sender.sendMessage(ChatColor.RED + "You may not go to faction headquarters from the Nether!");
            return;
        }

		FoxtrotPlugin.getInstance().getServerHandler().beginWarp(sender, team, 75);
	}

}