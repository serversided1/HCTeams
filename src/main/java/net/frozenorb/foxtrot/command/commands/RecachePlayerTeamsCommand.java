package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RecachePlayerTeamsCommand {

    @Command(names={ "playerteamcache rebuild" }, permissionNode="op")
    public static void recachePlayerTeamsRebuild(Player sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "Rebuilding player team cache...");
        FoxtrotPlugin.getInstance().getTeamHandler().recachePlayerTeams();
        sender.sendMessage(ChatColor.DARK_PURPLE + "The player death cache has been rebuilt.");
    }

    @Command(names={ "playerteamcache check" }, permissionNode="op")
    public static void recachePlayerTeams(Player sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "Checking player team cache...");
        Map<String, String> dealtWith = new HashMap<String, String>();
        Set<String> errors = new HashSet<String>();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            for (String member : team.getMembers()) {
                if (dealtWith.containsKey(member) && !errors.contains(member)) {
                    errors.add(member);
                    sender.sendMessage(ChatColor.RED + " - " + member + " (Team: " + team.getName() + ", Expected: " + dealtWith.get(member) + ")");
                    continue;
                }

                dealtWith.put(member, team.getName());
            }
        }

        if (errors.size() == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No errors found while checking player team cache.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + errors.size() + " error(s) found while checking player team cache.");
        }
    }

}