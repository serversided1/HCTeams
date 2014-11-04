package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Invite {

    @Command(names={ "team invite", "t invite", "f invite", "faction invite", "fac invite" }, permissionNode="")
    public static void teamInvite(Player sender, @Param(name="player") Player target) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Team.MAX_TEAM_SIZE) {
            sender.sendMessage(ChatColor.RED + "The max team size is " + Team.MAX_TEAM_SIZE + "!");
            return;
        }

        if (team.isOwner(sender.getName()) || team.isCaptain(sender.getName())) {
            if (!team.isMember(target.getName())) {
                if (team.getInvitations().contains(target.getName())) {
                    sender.sendMessage(ChatColor.RED + "That player has already been invited.");
                    return;
                }

                if (team.isRaidable()){
                    sender.sendMessage(ChatColor.RED + "You may not invite players if your team is raidable! You must boost your DTR!");
                    return;
                }

                team.getInvitations().add(target.getName());
                target.sendMessage(ChatColor.GRAY + "You have been invited to team '§e" + team.getFriendlyName() + "§7'. Type '§3/team join §e" + team.getFriendlyName() + "§7' to join.");
                sender.sendMessage("§e" + target.getName() + " has been invited to the team!");
            } else {
                sender.sendMessage(ChatColor.DARK_AQUA + "Player is already on your team.");
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
	}

}