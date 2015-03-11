package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.UUIDUtils;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceLeaderCommand {

    @Command(names={ "ForceLeader" }, permissionNode="foxtrot.forceleader")
    public static void forceLeader(Player sender, @Parameter(name="Team") Team team,  @Parameter(name="Target") UUID target) {
        if (!FoxtrotPlugin.getInstance().getPlaytimeMap().hasPlayed(target)) {
            sender.sendMessage(ChatColor.RED + "That player has never played here!");
            return;
        }

        if (!team.isMember(target)) {
            sender.sendMessage(ChatColor.RED + "That player is not a member of " + team.getName() + ".");
            return;
        }

        team.setOwner(target);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + UUIDUtils.name(target) + ChatColor.YELLOW + " is now the owner of " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}