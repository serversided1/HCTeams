package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceDisbandCommand {

    @Command(names={ "forcedisband" }, permissionNode="worldedit.*")
    public static void forceDisband(Player sender, @Parameter(name="team") Team target) {
        target.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force disbanded the team.");
        target.disband();
        sender.sendMessage(ChatColor.YELLOW + "Force disbanded the team " + ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + ".");
    }

}