package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ForceKickCommand {

    @Command(names={ "forcekick" }, permissionNode="worldedit.*")
    public static void forceKick(Player sender, @Parameter(name="player") UUID player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on a team!");
            return;
        }

        if (team.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + "'s team has one member. Please use /forcedisband to perform this action.");
            return;
        }

        team.removeMember(player);
        Foxtrot.getInstance().getTeamHandler().setTeam(player, null);

        sender.sendMessage(ChatColor.YELLOW + "Force kicked " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(player) + ChatColor.YELLOW + " from their team, " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}