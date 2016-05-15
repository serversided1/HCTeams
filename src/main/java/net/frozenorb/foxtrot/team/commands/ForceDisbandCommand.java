package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceDisbandCommand {

    @Command(names={ "forcedisband" }, permission="op")
    public static void forceDisband(Player sender, @Param(name="team") Team team) {
        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force disbanded the team.");
        team.disband();
        sender.sendMessage(ChatColor.YELLOW + "Force disbanded the team " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}