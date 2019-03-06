package net.frozenorb.foxtrot.team.commands.team;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.UUIDUtils;

public class TeamTopCommand {

    @Command(names={ "team top", "t top", "f top", "faction top", "fac top" }, permission="")
    public static void teamList(final CommandSender sender) {
        // This is sort of intensive so we run it async (cause who doesn't love async!)
        new BukkitRunnable() {

            public void run() {
                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = getSortedTeams();

                int index = 0;

                sender.sendMessage(Team.GRAY_LINE);

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                    
                    if (teamEntry.getKey().getOwner() == null) {
                        continue;
                    }
                    
                    index++;

                    if (10 <= index) {
                        break;
                    }

                    FancyMessage teamMessage = new FancyMessage();

                    Team team = teamEntry.getKey();
                    
                    teamMessage.text(index + ". ").color(ChatColor.GRAY).then();
                    teamMessage.text(teamEntry.getKey().getName()).color(sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
                    .tooltip((sender instanceof Player && teamEntry.getKey().isMember(((Player) sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED).toString() + teamEntry.getKey().getName() + "\n" +
                    ChatColor.LIGHT_PURPLE + "Leader: " + ChatColor.GRAY + UUIDUtils.name(teamEntry.getKey().getOwner()) + "\n\n" +
                            ChatColor.LIGHT_PURPLE + "Balance: " + ChatColor.GRAY + "$" + team.getBalance() + "\n" +
                    ChatColor.LIGHT_PURPLE + "Kills: " + ChatColor.GRAY.toString() + team.getKills() + "\n" +
                            ChatColor.LIGHT_PURPLE + "Deaths: " + ChatColor.GRAY.toString() + team.getDeaths() + "\n\n" +
                    ChatColor.LIGHT_PURPLE + "KOTH Captures: " + ChatColor.GRAY.toString() + team.getKothCaptures() + "\n" +
                            ChatColor.LIGHT_PURPLE + "Diamonds Mined: " + ChatColor.GRAY.toString() + team.getDiamondsMined() + "\n\n" +
                     ChatColor.GREEN + "Click to view team info").command("/t who " + teamEntry.getKey().getName()).then();
                    teamMessage.text(" - ").color(ChatColor.YELLOW).then();
                    teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.GRAY);

                    teamMessage.send(sender);
                }

                sender.sendMessage(Team.GRAY_LINE);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public static LinkedHashMap<Team, Integer> getSortedTeams() {
        Map<Team, Integer> teamPointsCount = new HashMap<>();

        // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            teamPointsCount.put(team, team.getPoints());
        }

        return sortByValues(teamPointsCount);
    }

    public static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map) {
        LinkedList<java.util.Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Team, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

}
