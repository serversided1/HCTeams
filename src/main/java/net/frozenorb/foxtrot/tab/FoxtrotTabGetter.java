package net.frozenorb.foxtrot.tab;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHScheduledTime;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.tab.TabEntry;
import net.frozenorb.qlib.tab.TabInfo;
import net.frozenorb.qlib.tab.TabInfoProvider;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class FoxtrotTabGetter implements TabInfoProvider {

    @Override
    public TabInfo getTabInfo(Player player) {
        TabInfo info = new TabInfo();

        info.addEntry(TabEntry.of(ChatColor.GOLD + "HCTeams.com", 0), 1, 0);

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        info.addEntry(TabEntry.of(ChatColor.RED + "Home:", 0), 0, 0);
        if (team == null || team.getHQ() == null) {
            info.addEntry(TabEntry.of(ChatColor.BLUE + "None", 0), 0, 1);
        } else {
            String homeLocation = team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();
            info.addEntry(TabEntry.of(ChatColor.BLUE + homeLocation, 0), 0, 1);
        }

        info.addEntry(TabEntry.of(ChatColor.RED + "Team Info:", 0), 0, 3);
        if (team != null) {
            info.addEntry(TabEntry.of(ChatColor.BLUE + "DTR: " + Team.DTR_FORMAT.format(team.getDTR()), 0), 0, 4);
            info.addEntry(TabEntry.of(ChatColor.BLUE + "Online: " + team.getOnlineMemberAmount(), 0), 0, 5);
            info.addEntry(TabEntry.of(ChatColor.BLUE + "Total: " + team.getMembers().size(), 0), 0, 6);
        } else {
            info.addEntry(TabEntry.of(ChatColor.BLUE + "No Team", 0), 0, 4);
        }

        info.addEntry(TabEntry.of(ChatColor.RED + "Player Info:", 0), 0, 8);
        info.addEntry(TabEntry.of(ChatColor.BLUE + "Kills: " + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()), 0), 0, 9);
        info.addEntry(TabEntry.of(ChatColor.BLUE + "Deaths: " + Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId()), 0), 0, 10);

        info.addEntry(TabEntry.of(ChatColor.RED + "Events:", 0), 0, 12);

        KOTH koth = null;
        for (KOTH k : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (k.isActive()) {

                koth = k;
                break;
            }
        }

        if (koth == null) {
            info.addEntry(TabEntry.of(ChatColor.BLUE + "N/A", 0), 0, 13);

            // TODO: Show next KOTH and in how long it'll go active
            for (Map.Entry<KOTHScheduledTime, String> entry : Foxtrot.getInstance().getKOTHHandler().getKOTHSchedule().entrySet()) {

            }
        } else {
            info.addEntry(TabEntry.of(ChatColor.BLUE + koth.getName(), 0), 0, 13);
            info.addEntry(TabEntry.of(ChatColor.YELLOW.toString() + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ(), 0), 0, 14);

            if (koth.getCurrentCapper() != null) {
                info.addEntry(TabEntry.of(ChatColor.YELLOW + TimeUtils.formatIntoHHMMSS(koth.getRemainingCapTime()), 0), 0, 15);
            }
        }


        // verification testing

        info.addEntry(TabEntry.of(ChatColor.RED + "Team Info:", 0), 0, 2);
        info.addEntry(TabEntry.of(ChatColor.RED + "Members Online", 0), 0, 3);

        if (team != null) {
            info.addEntry(TabEntry.of(player), 1, 4);

            int x = 1;
            int y = 5;
            for (Player member : team.getOnlineMembers()) {
                if (member.equals(player)) {
                    continue;
                }

                info.addEntry(TabEntry.of(player), x, y);

                y++;

                if (y > 20) {
                    y = 0;
                    x++;
                }
            }
        }

        return info;
    }

}
