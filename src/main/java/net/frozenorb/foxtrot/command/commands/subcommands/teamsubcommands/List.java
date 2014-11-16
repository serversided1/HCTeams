package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map;

/**
 * Created by macguy8 on 11/2/2014.
 */
public class List {

    @Command(names={ "team hq", "t list", "f list", "faction list", "fac list" }, permissionNode="")
    public static void teamForceLeave(Player sender, @Param(name="page", defaultValue="1") int page) {
        if (page < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
            return;
        }

        HashMap<Team, Integer> teamPlayerCount = new HashMap<Team, Integer>();

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

            if (playerTeam != null) {
                if (teamPlayerCount.containsKey(playerTeam)) {
                    teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                } else {
                    teamPlayerCount.put(playerTeam, 1);
                }
            }
        }

        int maxPages = teamPlayerCount.size() / 10;

        maxPages++;

        if (page > maxPages) {
            page = maxPages;
        }

        LinkedHashMap<Team, Integer> sortedTeamPlayerCount = sortByValues(teamPlayerCount);

        String gray = "§7§m" + StringUtils.repeat("-", 53);
        int start = (page - 1) * 10;
        int index = 0;

        sender.sendMessage(gray);
        sender.sendMessage(ChatColor.BLUE + "Faction List " +  ChatColor.GRAY + "(Page " + page + "/" + maxPages + ")");

        for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
            index++;

            if (index < start) {
                continue;
            }

            if (index > start + 10) {
                break;
            }

            sender.sendMessage(ChatColor.GRAY.toString() + (index) + ". " + ChatColor.YELLOW + teamEntry.getKey().getFriendlyName() + ChatColor.GREEN + " (" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + ")");
        }

        sender.sendMessage(ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + page + "/" + maxPages + ChatColor.GRAY + ".");
        sender.sendMessage(ChatColor.GRAY + "To view other pages, use " + ChatColor.YELLOW + "/f list <page#>" + ChatColor.GRAY + ".");
        sender.sendMessage(gray);
    }

    private static LinkedHashMap<Team, Integer> sortByValues(HashMap<Team, Integer> map) {
        LinkedList<java.util.Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<Team, Integer>>() {

            public int compare(java.util.Map.Entry<Team, Integer> o1, java.util.Map.Entry<Team, Integer> o2) {
                return (o2.getValue().compareTo(o1.getValue()));
            }

        });

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<Team, Integer>();
        Iterator<Map.Entry<Team, Integer>> iterator = list.iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry<Team, Integer> entry = iterator.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

}