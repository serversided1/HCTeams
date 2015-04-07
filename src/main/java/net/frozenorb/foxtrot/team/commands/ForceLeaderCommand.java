package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceLeaderCommand {

    @Command(names={ "ForceLeader" }, permissionNode="foxtrot.forceleader")
    public static void forceLeader(Player sender, @Parameter(name="team") Team team,  @Parameter(name="player") UUID player) {
        if (!Foxtrot.getInstance().getPlaytimeMap().hasPlayed(player)) {
            sender.sendMessage(ChatColor.RED + "That player has never played here!");
            return;
        }

        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.RED + "That player is not a member of " + team.getName() + ".");
            return;
        }

        team.setOwner(player);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + " is now the owner of " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}