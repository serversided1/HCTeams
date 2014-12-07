package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamCommand {

    @Command(names={ "team", "t", "f", "faction", "fac" }, permissionNode="")
    public static void team(Player sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "***Anyone***");
        sender.sendMessage(ChatColor.GRAY + "/t accept <teamName> - Accept a pending invitation.");
        sender.sendMessage(ChatColor.GRAY + "/t create [teamName] - Create a team.");
        sender.sendMessage(ChatColor.GRAY + "/t leave - Leave your current team.");
        sender.sendMessage(ChatColor.GRAY + "/t who [playerName/teamName] - Get details about a team.");
        sender.sendMessage(ChatColor.GRAY + "/t chat - Toggle team chat mode on or off.");
        sender.sendMessage(ChatColor.GRAY + "/t hq - Teleport to the team HQ.");
        sender.sendMessage(ChatColor.GRAY + "/t deposit <amount> - Deposit money to team balance.");
        sender.sendMessage(ChatColor.GRAY + "/t map - View the boundaries of teams near you.");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_AQUA + "***Team Captains Only***");
        sender.sendMessage(ChatColor.GRAY + "/t kick [player] - Kick a player from the team.");
        sender.sendMessage(ChatColor.GRAY + "/t claim - Receive the claiming wand.");
        sender.sendMessage(ChatColor.GRAY + "/t uninvite - Manage pending invitations.");
        sender.sendMessage(ChatColor.GRAY + "/t invite <player> - Invite a player to the team.");
        sender.sendMessage(ChatColor.GRAY + "/t sethq - Set the team HQ location.");
        sender.sendMessage(ChatColor.GRAY + "/t withdraw <amount> - Withdraw money from team balance.");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_AQUA + "***Team Owner Only***");
        sender.sendMessage(ChatColor.GRAY + "/t promote - Promotes the targeted player to a captain.");
        sender.sendMessage(ChatColor.GRAY + "/t demote - Demotes the targeted player to a member.");
        sender.sendMessage(ChatColor.GRAY + "/t unclaim - Unclaim land.");
        sender.sendMessage(ChatColor.GRAY + "/t leader [playerName] - Gives a player leader of your team.");
        sender.sendMessage(ChatColor.GRAY + "/t disband - Disband the team. [Warning]");
    }

}