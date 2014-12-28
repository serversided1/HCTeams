package net.frozenorb.foxtrot.ctf.commands.ctf;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.ctf.CTFHandler;
import net.frozenorb.foxtrot.ctf.enums.CTFFlagColor;
import net.frozenorb.foxtrot.ctf.game.CTFGame;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class CTFScoreCommand {

    @Command(names={ "ctf score", "ctf scores" }, permissionNode="")
    public static void ctfScore(Player sender, @Param(name="page", defaultValue="1") int page) {
        if (page < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
            return;
        }

        CTFGame game = FoxtrotPlugin.getInstance().getCTFHandler().getGame();

        if (game == null) {
            sender.sendMessage(CTFHandler.PREFIX + " " + ChatColor.YELLOW + "There isn't an active CTF game!");
            return;
        }

        Map<ObjectId, Set<CTFFlagColor>> capturedFlags = game.getCapturedFlags();
        Map<Team, Integer> teamFlagCount = new HashMap<Team, Integer>();

        for (Map.Entry<ObjectId, Set<CTFFlagColor>> teamCapturedFlagsEntry : capturedFlags.entrySet()) {
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getTeam(teamCapturedFlagsEntry.getKey());

            if (team != null) {
                teamFlagCount.put(team, teamCapturedFlagsEntry.getValue().size());
            }
        }

        int maxPages = teamFlagCount.size() / 10;

        maxPages++;

        if (page > maxPages) {
            page = maxPages;
        }

        LinkedHashMap<Team, Integer> sortedTeamFlagCount = sortByValues(teamFlagCount);

        int start = (page - 1) * 10;
        int index = 0;

        sender.sendMessage(Team.GRAY_LINE);
        sender.sendMessage(ChatColor.BLUE + "CTF Score " +  ChatColor.GRAY + "(Page " + page + "/" + maxPages + ")");

        for (Map.Entry<Team, Integer> teamEntry : sortedTeamFlagCount.entrySet()) {
            index++;

            if (index < start) {
                continue;
            }

            if (index > start + 10) {
                break;
            }

            Set<CTFFlagColor> cappedFlags = capturedFlags.get(teamEntry.getKey().getUniqueId());
            StringBuilder missingBuilder = new StringBuilder();

            for (CTFFlagColor flagColor : CTFFlagColor.values()) {
                if (!cappedFlags.contains(flagColor)) {
                    missingBuilder.append(flagColor.getName()).append(", ");
                }
            }

            if (missingBuilder.length() > 2) {
                missingBuilder.setLength(missingBuilder.length() - 2);
            }

            sender.sendMessage(ChatColor.GRAY.toString() + (index) + ". " + ChatColor.YELLOW + teamEntry.getKey().getName() + ChatColor.GREEN + " (" + teamEntry.getValue() + "/" + CTFFlagColor.values().length + ")" + ChatColor.DARK_AQUA + " - " + ChatColor.YELLOW + "Missing " + missingBuilder.toString());
        }

        sender.sendMessage(ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + page + "/" + maxPages + ChatColor.GRAY + ".");
        sender.sendMessage(ChatColor.GRAY + "To view other pages, use " + ChatColor.YELLOW + "/ctf score <page#>" + ChatColor.GRAY + ".");
        sender.sendMessage(Team.GRAY_LINE);
    }

    private static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map) {
        LinkedList<Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());

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