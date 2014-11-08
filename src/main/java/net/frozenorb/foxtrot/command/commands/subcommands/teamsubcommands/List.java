package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;

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

        int factionsWithMembersOnline = 0;
        ArrayList<Team> sortedByOnline = new ArrayList<Team>();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            if (team.getOnlineMemberAmount() != 0) {
                factionsWithMembersOnline++;
            }

            sortedByOnline.add(team);
        }

        int maxPages = factionsWithMembersOnline / 10;

        maxPages++;

        if (page > maxPages) {
            page = maxPages;
        }

        sortedByOnline.sort(new Comparator<Team>() {

            @Override
            public int compare(Team o1, Team o2) {
                return (Integer.valueOf(o2.getOnlineMemberAmount()).compareTo(o1.getOnlineMemberAmount()));
            }

        });

        String gray = "§7§m" + StringUtils.repeat("-", 53);
        int start = (page - 1) * 10;

        sender.sendMessage(gray);
        sender.sendMessage(ChatColor.BLUE + "Faction List " +  ChatColor.GRAY + "(Page " + page + "/" + maxPages + ")");

        for (int i = start; i < start + 10; i++) {
            try {
                Team team = sortedByOnline.get(i);
                int online = team.getOnlineMemberAmount();

                if (online != 0) {
                    sender.sendMessage(ChatColor.GRAY.toString() + (i + 1) + ". " + ChatColor.YELLOW + team.getFriendlyName() + ChatColor.GREEN + " (" + online + "/" + team.getSize() + ")");
                }
            } catch (Exception e) {

            }
        }

        sender.sendMessage(ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + page + "/" + maxPages + ChatColor.GRAY + ".");
        sender.sendMessage(ChatColor.GRAY + "To view other pages, use " + ChatColor.YELLOW + "/f list <page#>" + ChatColor.GRAY + ".");
        sender.sendMessage(gray);
    }

}