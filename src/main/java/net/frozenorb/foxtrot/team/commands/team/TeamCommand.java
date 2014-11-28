package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamCommand {

    @Command(names={ "team", "t", "f", "faction", "fac" }, permissionNode="")
    public static void team(Player sender) {
        sender.sendMessage(ChatColor.DARK_AQUA + "***Anyone***");
        sender.sendMessage(ChatColor.GRAY + "/f accept <teamName> - Accept a pending invitation.");
        sender.sendMessage(ChatColor.GRAY + "/f create [teamName] - Create a faction.");
        sender.sendMessage(ChatColor.GRAY + "/f leave - Leave your current faction.");
        sender.sendMessage(ChatColor.GRAY + "/f roster <team> - Get details about the faction.");
        sender.sendMessage(ChatColor.GRAY + "/f info [playerName] - Get details about a faction.");
        sender.sendMessage(ChatColor.GRAY + "/f chat - Toggle faction chat only mode on or off.");
        sender.sendMessage(ChatColor.GRAY + "/f claims [team] - View all claims for a faction.");
        sender.sendMessage(ChatColor.GRAY + "/f msg <message> - Sends a message to your faction.");
        sender.sendMessage(ChatColor.GRAY + "/f hq - Teleport to the team headquarters.");
        sender.sendMessage(ChatColor.GRAY + "/f deposit <amount> - Deposit money to team balance.");
        sender.sendMessage(ChatColor.GRAY + "/f map - View the boundaries of factions near you.");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_AQUA + "***Faction Captains Only***");
        sender.sendMessage(ChatColor.GRAY + "/f kick [player] - Kick a player from the faction.");
        sender.sendMessage(ChatColor.GRAY + "/f claim - Receive the claiming wand.");
        sender.sendMessage(ChatColor.GRAY + "/f uninvite - Manage pending invitations.");
        sender.sendMessage(ChatColor.GRAY + "/f invite <player> - Invite a player to the team.");
        sender.sendMessage(ChatColor.GRAY + "/f sethq - Set the faction headquarters warp location.");
        sender.sendMessage(ChatColor.GRAY + "/f withdraw <amount> - Withdraw money from team balance.");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_AQUA + "***Faction Leader Only***");
        sender.sendMessage(ChatColor.GRAY + "/f promote - Promotes the targeted player to a captain.");
        sender.sendMessage(ChatColor.GRAY + "/f demote - Demotes the targeted player to a member.");
        sender.sendMessage(ChatColor.GRAY + "/f unclaim - Unclaim land.");
        sender.sendMessage(ChatColor.GRAY + "/f newleader [playerName] - Make a player an owner on your faction.");
        sender.sendMessage(ChatColor.GRAY + "/f disband - Disband the faction. [Warning]");
    }

}