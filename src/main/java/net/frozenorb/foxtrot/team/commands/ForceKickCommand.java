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
    public static void forceKick(Player sender, @Parameter(name="player") UUID target) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(target);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(target) + " is not on a team!");
            return;
        }

        if (team.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(target) + "'s team has one member. Please use /forcedisband to perform this action.");
            return;
        }

        team.removeMember(target);
        Foxtrot.getInstance().getTeamHandler().setTeam(target, null);

        sender.sendMessage(ChatColor.YELLOW + "Force kicked " + ChatColor.LIGHT_PURPLE + UUIDUtils.name(target) + ChatColor.YELLOW + " from their team, " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}