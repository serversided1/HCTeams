package net.frozenorb.foxtrot.util;

import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class ClickableUtils {

    private ClickableUtils() {}

    public static FancyMessage clickableName(String player) {
        FancyMessage appendTo = new FancyMessage();
        appendClickableName(player, appendTo);
        return (appendTo);
    }

    public static void appendClickableName(String player, FancyMessage appendTo) {
        appendTo.text(player).link("http://www.hcteams.com/player/" + ChatColor.stripColor(player)).tooltip(ChatColor.GREEN + "Click to view " + player + ChatColor.GREEN + "'s profile").then();
    }

    public static FancyMessage clickableTeam(String team) {
        FancyMessage appendTo = new FancyMessage();
        appendClickableTeam(team, appendTo);
        return (appendTo);
    }

    public static void appendClickableTeam(String team, FancyMessage appendTo) {
        appendTo.text(team).link("http://www.hcteams.com/team/" + ChatColor.stripColor(team)).tooltip(ChatColor.GREEN + "Click to view " + team + ChatColor.GREEN + " on the HCTeams website").then();
    }

}