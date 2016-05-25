package net.frozenorb.foxtrot.tab;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabEntry;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FoxtrotTabGetter implements LayoutProvider {

    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        layout.set(1, 0, TabEntry.of(ChatColor.GOLD.toString() + ChatColor.BOLD + "HCTeams.com"));

        layout.set(0, 0, TabEntry.of(ChatColor.RED + "Home:"));
        if (team == null || team.getHQ() == null) {
            layout.set(0, 1, TabEntry.of(ChatColor.BLUE + "None"));
        } else {
            String homeLocation = team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();

            layout.set(0, 1, TabEntry.of(homeLocation));
        }

        layout.set(0, 3, TabEntry.of(ChatColor.RED + "Team Info:"));
        if (team == null) {
            layout.set(0, 4, TabEntry.of(ChatColor.BLUE + "No Team"));
        } else {
            layout.set(0, 4, TabEntry.of(ChatColor.BLUE + "DTR: " + Team.DTR_FORMAT.format(team.getDTR())));
            layout.set(0, 5, TabEntry.of(ChatColor.BLUE + "Online: " + team.getOnlineMemberAmount()));
            layout.set(0, 6, TabEntry.of(ChatColor.BLUE + "Total: " + team.getMembers().size()));
        }

        layout.set(0, 8, TabEntry.of(ChatColor.RED + "Player Info:"));
        layout.set(0, 9, TabEntry.of(ChatColor.BLUE + "Kills: " + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId())));
        layout.set(0, 10, TabEntry.of(ChatColor.BLUE + "Deaths: " + Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId())));

        layout.set(0, 12, TabEntry.of(ChatColor.RED + "Events:"));

        KOTH activeKOTH = null;
        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (koth.isActive()) {
                activeKOTH = koth;
                break;
            }
        }

        if (activeKOTH == null) {
            layout.set(0, 13, TabEntry.of(ChatColor.BLUE + "N/A"));

            // TODO: Show next KOTH (pull it from KOTH schedule) and in how long it's going active
        } else {
            layout.set(0, 13, TabEntry.of(ChatColor.BLUE + activeKOTH.getName()));
            layout.set(0, 14, TabEntry.of(ChatColor.YELLOW.toString() + activeKOTH.getCapLocation().getBlockX() + ", " + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation().getBlockZ())); // location
            if (activeKOTH.getCurrentCapper() != null) {
                layout.set(0, 15, TabEntry.of(ChatColor.YELLOW + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime())));
            }
        }

        layout.set(1, 2, TabEntry.of(ChatColor.RED + "Team Members Online:"));

        if (team != null) {
            layout.set(1, 3, TabEntry.of(player));

            int x = 1;
            int y = 4;

            for (Player member : team.getOnlineMembers()) {
                if (member.equals(player)) {
                    continue;
                }

                layout.set(x, y, TabEntry.of(player));

                y++;

                if (y > 20) {
                    y = 0;
                    x++;
                }
            }
        }

        return layout;
    }

}
