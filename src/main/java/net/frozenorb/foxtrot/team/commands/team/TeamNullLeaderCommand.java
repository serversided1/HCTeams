package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamNullLeaderCommand {

    @Command(names={ "team nullleader", "t nullleader", "f nullleader", "faction nullleader", "fac nullleader" }, permissionNode="op")
    public static void teamSaveString(Player sender) {
        int nullLeaders = 0;

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() == null || team.getOwner().equals("null")) {
                nullLeaders++;
                sender.sendMessage(ChatColor.RED + "- " + team.getName());
            }
        }

        if (nullLeaders == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No null teams found.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + nullLeaders + " null teams found.");
        }
    }

}