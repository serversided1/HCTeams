package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaderCommand {

    @Command(names={ "ForceLeader" }, permissionNode="foxtrot.forceleader")
    public static void forceLeader(Player sender, @Parameter(name="Team", defaultValue="self") Team team,  @Parameter(name="Target", defaultValue="self") String target) {
        if (target.equals("self")) {
            target = sender.getName();
        } else if (target.equals("null")) {
            target = null;
        }

        if (target != null && !FoxtrotPlugin.getInstance().getPlaytimeMap().contains(target)) {
            sender.sendMessage(ChatColor.RED + "That player has never played here!");
        } else {
            if (target != null && !team.isMember(target)) {
                sender.sendMessage(ChatColor.RED + "That player is not a member of " + team.getName() + ".");
                return;
            }

            team.setOwner(target);
            sender.sendMessage(ChatColor.LIGHT_PURPLE + target + ChatColor.YELLOW + " is now the owner of " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
        }
    }

}