package net.frozenorb.foxtrot.util;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;

public class ClickableUtils {

    private ClickableUtils() {}

    public static FancyMessage deathMessageName(String player) {
        FancyMessage appendTo = new FancyMessage();
        appendDeathMessageName(player, appendTo);
        return (appendTo);
    }

    public static void appendDeathMessageName(String player, FancyMessage appendTo) {
        appendTo.text(ChatColor.RED + player).link("http://www.hcteams.com/player/" + player).then().text(ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(player) + "]").then();
    }

    public static FancyMessage clickableName(String player) {
        FancyMessage appendTo = new FancyMessage();
        appendClickableName(player, appendTo);
        return (appendTo);
    }

    public static void appendClickableName(String player, FancyMessage appendTo) {
        appendTo.text(player).link("http://www.hcteams.com/player/" + player).then();
    }

    public static FancyMessage clickableTeam(Team team) {
        FancyMessage appendTo = new FancyMessage();
        appendClickableTeam(team, appendTo);
        return (appendTo);
    }

    public static void appendClickableTeam(Team team, FancyMessage appendTo) {

    }

}