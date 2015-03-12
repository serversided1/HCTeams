package net.frozenorb.foxtrot;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.ClickableUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FoxConstants {

    private static final Pattern URL_PATTERN = Pattern.compile("(" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + "[0-9a-fk-or])|(\\n)|((?:(?:https?)://)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + " \\n]|$))))", Pattern.CASE_INSENSITIVE);

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

    public static FancyMessage publicChatFormat(Team team, String highRollerString, String customPrefixString, ChatColor teamColor, Player player, String message) {
        FancyMessage chat = new FancyMessage(ChatColor.GOLD + "[").then();

        if (team == null) {
            chat.text(teamColor + "-").then();
        } else {
            ClickableUtils.appendClickableTeam(teamColor + team.getName(), chat);
        }

        chat.text(ChatColor.GOLD + "]" + highRollerString + customPrefixString).then();
        ClickableUtils.appendClickableName(player.getDisplayName(), chat);
        chat.text(ChatColor.WHITE + ": ");

        Matcher matcher = URL_PATTERN.matcher(message);
        String match = null;
        int currentIndex = 0;

        while (matcher.find()) {
            int groupId = 0;

            while ((match = matcher.group(++groupId)) == null) {
                // NOOP
            }

            int index = (matcher.start(groupId));
            String part = message.substring(currentIndex, index);
            currentIndex = index;
            chat.then(part);

            switch (groupId) {
                case 3:
                    if (!(match.startsWith("http://") || match.startsWith("https://"))) {
                        match = "http://" + match;
                    }

                    int newIndex = matcher.end(groupId);
                    String tp = message.substring(currentIndex, newIndex);
                    currentIndex = newIndex;
                    chat.then(tp);
                    chat.link(match).tooltip("§aOpen webpage!");
            }

            currentIndex = matcher.end(groupId);
        }

        if (currentIndex < message.length()) {
            chat.then(message);
        }

        return (chat);
    }

}