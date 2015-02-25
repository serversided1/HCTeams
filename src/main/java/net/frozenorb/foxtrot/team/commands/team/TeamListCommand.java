package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TeamListCommand {

    @Command(names={ "team list", "t list", "f list", "faction list", "fac list" }, permissionNode="")
    public static void teamList(final Player sender, @Param(name="page", defaultValue="1") final int page) {
        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            public void run() {
                if (page < 1) {
                    sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
                    return;
                }

                Map<Team, Integer> teamPlayerCount = new HashMap<Team, Integer>();

                // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (player.hasMetadata("invisible")) {
                        continue;
                    }

                    Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

                    if (playerTeam != null) {
                        if (teamPlayerCount.containsKey(playerTeam)) {
                            teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                        } else {
                            teamPlayerCount.put(playerTeam, 1);
                        }
                    }
                }

                int maxPages = (teamPlayerCount.size() / 10) + 1;
                int currentPage = Math.min(page, maxPages);

                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = sortByValues(teamPlayerCount);

                int start = (currentPage - 1) * 10;
                int index = 0;

                sender.sendMessage(Team.GRAY_LINE);
                sender.sendMessage(ChatColor.BLUE + "Team List " +  ChatColor.GRAY + "(Page " + currentPage + "/" + maxPages + ")");

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                    index++;

                    if (index < start) {
                        continue;
                    }

                    if (index > start + 10) {
                        break;
                    }

                    sender.sendMessage(ChatColor.GRAY.toString() + (index) + ". " + ChatColor.YELLOW + teamEntry.getKey().getName() + ChatColor.GREEN + " (" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + ")");
                }

                sender.sendMessage(ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + currentPage + "/" + maxPages + ChatColor.GRAY + ".");
                sender.sendMessage(ChatColor.GRAY + "To view other pages, use " + ChatColor.YELLOW + "/t list <page#>" + ChatColor.GRAY + ".");
                sender.sendMessage(Team.GRAY_LINE);
            }

        }.runTaskAsynchronously(FoxtrotPlugin.getInstance());
    }

    private static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map) {
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