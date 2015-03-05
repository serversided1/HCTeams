package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ForceKickCommand {

    @Command(names={ "forcekick" }, permissionNode="op")
    public static void forceKick(Player sender, @Parameter(name="player") OfflinePlayer player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on a team!");
            return;
        }

        if (team.getMembers().size() == 1) {
            sender.sendMessage(ChatColor.RED + player.getName() + "'s team has one member. Please use /forcedisband to perform this action.");
            return;
        }

        team.removeMember(player.getUniqueId());
        FoxtrotPlugin.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);

        sender.sendMessage(ChatColor.YELLOW + "Force kicked " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " from their team, " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}