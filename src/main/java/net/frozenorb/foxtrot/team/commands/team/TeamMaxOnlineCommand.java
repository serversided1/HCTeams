package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamMaxOnlineCommand {

    @Command(names={ "team maxOnline" }, permissionNode="op")
    public static void teamMaxOnline(Player sender, @Parameter(name="team") Team team, @Parameter(name="max online", defaultValue="-1") int maxOnline) {
        if (maxOnline == -1) {
            if (team.getMaxOnline() == -1) {
                sender.sendMessage(team.getName(sender) + ChatColor.YELLOW + "'s player limit is " + ChatColor.GREEN + "not set" + ChatColor.YELLOW + ".");
            } else {
                sender.sendMessage(team.getName(sender) + ChatColor.YELLOW + "'s player limit is " + ChatColor.RED + team.getMaxOnline() + ChatColor.YELLOW + ".");
            }
        } else {
            team.setMaxOnline(maxOnline);
            sender.sendMessage(team.getName(sender) + ChatColor.YELLOW + "'s player limit has been set to " + ChatColor.RED + maxOnline + ChatColor.YELLOW + ".");
        }
    }

}