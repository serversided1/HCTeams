package net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by macguy8 on 11/2/2014.
 */
public class List {

    @Command(names={ "team hq", "t list", "f list", "faction list", "fac list" }, permissionNode="")
    public static void teamForceLeave(Player sender, @Param(name="Page", defaultValue="1") int page) {
        try {
            if (page < 1) {
                sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
                return;
            }

            ArrayList<Team> sortedByOnline = new ArrayList<Team>();

            for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
                sortedByOnline.add(team);
            }

            sortedByOnline.sort(new Comparator<Team>() {

                @Override
                public int compare(Team o1, Team o2) {
                    return (Integer.valueOf(o2.getOnlineMemberAmount()).compareTo(o1.getOnlineMemberAmount()));
                }

            });

            int start = (page - 1) * 8;

            for (int i = start; i < start + 8; i++) {
                sender.sendMessage(ChatColor.YELLOW + sortedByOnline.get(i).getFriendlyName() + ": " + ChatColor.GRAY + sortedByOnline.get(i).getOnlineMemberAmount() + "/" + sortedByOnline.get(i).getSize());
            }
        } catch (Exception e) {
            // Silently catch.
        }
    }

}