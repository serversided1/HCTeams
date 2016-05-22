package net.frozenorb.foxtrot.tab;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.koth.KOTHScheduledTime;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.tab.TabEntry;
import net.frozenorb.qlib.tab.TabInfo;
import net.frozenorb.qlib.tab.TabInfoGetter;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class FoxtrotTabGetter implements TabInfoGetter {

    @Override
    public TabInfo getTabInfo(Player player) {
        TabInfo info = new TabInfo();

        info.addEntry(new TabEntry(ChatColor.GOLD + "HCTeams.com", false, 0), 1, 0);

        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team != null) {
            info.addEntry(new TabEntry(ChatColor.RED + "Home:", false, 0), 0, 0);

            if (team.getHQ() != null) {
                String homeLocation = team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();
                info.addEntry(new TabEntry(ChatColor.BLUE + homeLocation, false, 0), 0, 1);
            } else {
                info.addEntry(new TabEntry(ChatColor.RED + "Not Set", false, 0), 0, 1);
            }

            info.addEntry(new TabEntry(ChatColor.RED + "Faction Info:", false, 0), 0, 3);
            info.addEntry(new TabEntry(ChatColor.BLUE + "DTR: " + Team.DTR_FORMAT.format(team.getDTR()), false, 0), 0, 4);
            info.addEntry(new TabEntry(ChatColor.BLUE + "Online: " + team.getOnlineMemberAmount(), false, 0), 0, 5);
            info.addEntry(new TabEntry(ChatColor.BLUE + "Total: " + team.getMembers().size(), false, 0), 0, 6);

            info.addEntry(new TabEntry(ChatColor.RED + "Player Info:", false, 0), 0, 8);
            info.addEntry(new TabEntry(ChatColor.BLUE + "Kills: " + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()), false, 0), 0, 9);
            info.addEntry(new TabEntry(ChatColor.BLUE + "Deaths: " + Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId()), false, 0), 0, 10);

            info.addEntry(new TabEntry(ChatColor.RED + "Events:", false, 0), 0, 12);

            KOTH koth = null;
            for (KOTH k : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
                if (k.isActive()) {

                    koth = k;
                    break;
                }
            }

            if (koth == null) {
                info.addEntry(new TabEntry(ChatColor.BLUE + "N/A", false, 0), 0, 13);

                // TODO: Show next KOTH and in how long it'll go active
                for (Map.Entry<KOTHScheduledTime, String> entry : Foxtrot.getInstance().getKOTHHandler().getKOTHSchedule().entrySet()) {
                }
            } else {
                info.addEntry(new TabEntry(ChatColor.BLUE + koth.getName(), false, 0), 0, 13);
                info.addEntry(new TabEntry(ChatColor.YELLOW.toString() + koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ(), false, 0), 0, 14);

                if (koth.getCurrentCapper() != null) {
                    info.addEntry(new TabEntry(ChatColor.YELLOW + TimeUtils.formatIntoHHMMSS(koth.getRemainingCapTime()), false, 0), 0, 15);
                }
            }
        }

        return info;
    }

}
