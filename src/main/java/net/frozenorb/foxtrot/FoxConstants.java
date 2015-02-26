package net.frozenorb.foxtrot;

import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class FoxConstants {

    public static String teamChatFormat(Player player, String message) {
        return (ChatColor.DARK_AQUA + "(Team) " + player.getName() + ": " + ChatColor.YELLOW + message);
    }

    public static String teamChatSpyFormat(Team team, Player player, String message) {
        return (ChatColor.GOLD + "[" + ChatColor.DARK_AQUA + "TC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + ChatColor.DARK_AQUA + player.getName() + ": " + message);
    }

    public static String allyChatFormat(Player player, String message) {
        return (Team.ALLY_COLOR + "(Ally) " + player.getName() + ": " + ChatColor.YELLOW + message);
    }

    public static String allyChatSpyFormat(Team team, Player player, String message) {
        return (ChatColor.GOLD + "[" + Team.ALLY_COLOR + "AC: " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + Team.ALLY_COLOR + player.getName() + ": " + message);
    }

    public static String highRollerPrefix() {
        return (ChatColor.DARK_PURPLE + "[HighRoller]");
    }

    public static String publicChatFormat(Team team, String highRollerString, String customPrefixString) {
        if (team == null) {
            return (ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]" + highRollerString + customPrefixString + ChatColor.WHITE + "%s" + ChatColor.WHITE + ": %s");
        } else {
            return (ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]" + highRollerString + customPrefixString + ChatColor.WHITE + "%s" + ChatColor.WHITE + ": %s");
        }
    }

}