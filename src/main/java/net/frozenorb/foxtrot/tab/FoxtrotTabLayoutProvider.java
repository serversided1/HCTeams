package net.frozenorb.foxtrot.tab;

import com.google.common.collect.Lists;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.qlib.tab.LayoutProvider;
import net.frozenorb.qlib.tab.TabEntry;
import net.frozenorb.qlib.tab.TabLayout;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class FoxtrotTabLayoutProvider implements LayoutProvider {

    @Override
    public TabLayout provide(Player player) {
        TabLayout layout = TabLayout.create(player);
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        layout.set(1, 0, TabEntry.of(ChatColor.GOLD.toString() + ChatColor.BOLD + "HCTeams.com"));

        layout.set(0, 0, TabEntry.of(ChatColor.RED + "Home:"));
        if (team == null || team.getHQ() == null) {
            layout.set(0, 1, TabEntry.of(ChatColor.BLUE + "None"));
        } else {
            String homeLocation = ChatColor.BLUE.toString() + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ();

            layout.set(0, 1, TabEntry.of(homeLocation));
        }

        if (team == null) {
            layout.set(0, 3, TabEntry.of(ChatColor.RED + "No Team"));
        } else {
            layout.set(0, 3, TabEntry.of(ChatColor.RED + team.getName()));

            layout.set(0, 4, TabEntry.of(ChatColor.BLUE + "DTR: " + Team.DTR_FORMAT.format(team.getDTR())));
            layout.set(0, 5, TabEntry.of(ChatColor.BLUE + "Online: " + team.getOnlineMemberAmount()));
            layout.set(0, 6, TabEntry.of(ChatColor.BLUE + "Total: " + team.getMembers().size()));
        }

        layout.set(0, 8, TabEntry.of(ChatColor.RED + "Player Info:"));
        layout.set(0, 9, TabEntry.of(ChatColor.BLUE + "Kills: " + Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId())));
        layout.set(0, 10, TabEntry.of(ChatColor.BLUE + "Deaths: " + Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId())));

        layout.set(0, 12, TabEntry.of(ChatColor.RED + "Your Location:"));

        String location;

        Location loc = player.getLocation();
        Team ownerTeam = LandBoard.getInstance().getTeam(loc);

        if (ownerTeam != null) {
            location = ownerTeam.getName(player.getPlayer());
        } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(loc)) {
            location = ChatColor.GRAY + "The Wilderness";
        } else {
            location = ChatColor.RED + "Warzone";
        }

        layout.set(0, 13, TabEntry.of(location));

        layout.set(0, 15, TabEntry.of(ChatColor.RED + "Events:"));

        KOTH activeKOTH = null;
        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (koth.isActive()) {
                activeKOTH = koth;
                break;
            }
        }

        if (activeKOTH == null) {
            layout.set(0, 16, TabEntry.of(ChatColor.BLUE + "N/A"));

            // TODO: Show next KOTH (pull it from KOTH schedule) and in how long it's going active
        } else {
            layout.set(0, 16, TabEntry.of(ChatColor.BLUE + activeKOTH.getName()));
            layout.set(0, 17, TabEntry.of(ChatColor.YELLOW.toString() + activeKOTH.getCapLocation().getBlockX() + ", " + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation().getBlockZ())); // location
            layout.set(0, 18, TabEntry.of(ChatColor.YELLOW + TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime())));
        }

        layout.set(1, 2, TabEntry.of(ChatColor.RED + "Members Online"));

        if (team != null) {
            String watcherName = ChatColor.DARK_GREEN + player.getName();
            if (team.isOwner(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "**";
            } else if (team.isCaptain(player.getUniqueId())) {
                watcherName += ChatColor.GRAY + "*";
            }

            layout.set(1, 3, TabEntry.of(watcherName, ((CraftPlayer) player).getHandle().ping)); // the viewer is always first on the list

            Player owner = null;
            List<Player> captains = Lists.newArrayList();
            List<Player> members = Lists.newArrayList();
            for (Player member : team.getOnlineMembers()) {
                if (team.isOwner(member.getUniqueId())) {
                    owner = member;
                } else if (team.isCaptain(member.getUniqueId())) {
                    captains.add(member);
                } else {
                    members.add(member);
                }
            }

            int x = 1;
            int y = 4;

            // then the owner
            if (owner != null && !owner.equals(player)) {
                layout.set(x, y, TabEntry.of(ChatColor.DARK_GREEN + owner.getName() + ChatColor.GRAY + "**", ((CraftPlayer) owner).getHandle().ping));

                y++;

                if (y > 20) {
                    y = 0;
                    x++;
                }
            }

            // then the captains
            for (Player captain : captains) {
                if (captain.equals(player)) continue;

                layout.set(x, y, TabEntry.of(ChatColor.DARK_GREEN + captain.getName() + ChatColor.GRAY + "*", ((CraftPlayer) captain).getHandle().ping));

                y++;

                if (y > 20) {
                    y = 0;
                    x++;
                }
            }

            // and only then, normal members.
            for (Player member : members) {
                if (member.equals(player)) continue;

                if (member.equals(player)) {
                    continue;
                }

                layout.set(x, y, TabEntry.of(ChatColor.DARK_GREEN + member.getName(), ((CraftPlayer) member).getHandle().ping));

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
