package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceDisbandCommand {

    @Command(names={ "forcedisband" }, permissionNode="op")
    public static void forceDisband(Player sender, @Parameter(name="team") Team target) {
        for (Player online : target.getOnlineMembers()) {
            online.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force disbanded the team.");
        }

        target.disband();
        sender.sendMessage(ChatColor.YELLOW + "Force disbanded the team " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + ".");
    }

}