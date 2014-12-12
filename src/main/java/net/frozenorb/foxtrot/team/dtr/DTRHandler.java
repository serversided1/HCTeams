package net.frozenorb.foxtrot.team.dtr;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DTRHandler extends BukkitRunnable {

    private static final double[] BASE_DTR_INCREMENT = { 1.5, .5, .45, .4, .36,
            .33, .3, .27, .24, .22, .21, .2, .19, .18, .175, .17, .168, .166,
            .164, .162, .16, .158, .156, .154, .152, .15, .148, .146, .144,
            .142, .142, .142, .142, .142, .142 };
    private static final double[] MAX_DTR = { 1.01, 1.8, 2.2, 2.7, 3.2,
            3.4, 3.6, 3.8, 3.9, 4.18, 4.23, 4.36, 4.42, 4.59, 4.67, 4.72, 4.89, 4.92,
            5.04, 5.15, 5.29, 5.37, 5.48, 5.52, 5.6 };
    // TODO: Update this when we have the proper DTR values.

    private static Set<String> wasOnCooldown = new HashSet<String>();

    // * 3 is to 'speed up' DTR regen while keeping the ratios the same.
    // We're using this instead of changing the array incase we need to change this value
    // In the future.
    public static double getBaseDTRIncrement(int teamsize) {
        return (teamsize == 0 ? 0 : BASE_DTR_INCREMENT[teamsize - 1] * 3F);
    }

    public static double getMaxDTR(int teamsize) {
        return (teamsize == 0 ? 100D : MAX_DTR[teamsize - 1]);
    }

    public static boolean isOnCooldown(Team team) {
        return (team.getDTRCooldown() > System.currentTimeMillis());
    }

    public static boolean isRegenerating(Team team) {
        return (!isOnCooldown(team) && team.getDTR() != team.getMaxDTR());
    }

    public static void setCooldown(Team team) {
        wasOnCooldown.add(team.getName().toLowerCase());
    }

    @Override
    public void run() {
        Map<Team, Integer> playerOnlineMap = new HashMap<Team, Integer>();

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (player.hasMetadata("invisible")) {
                continue;
            }

            Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

            if (playerTeam != null) {
                if (playerOnlineMap.containsKey(playerTeam)) {
                    playerOnlineMap.put(playerTeam, playerOnlineMap.get(playerTeam) + 1);
                } else {
                    playerOnlineMap.put(playerTeam, 1);
                }
            }
        }

        for (Map.Entry<Team, Integer> teamEntry : playerOnlineMap.entrySet()) {
            if (teamEntry.getKey().getOwner() != null) {
                try {
                    if (isOnCooldown(teamEntry.getKey())) {
                        wasOnCooldown.add(teamEntry.getKey().getName().toLowerCase());
                        continue;
                    }

                    if (wasOnCooldown.contains(teamEntry.getKey().getName().toLowerCase())) {
                        wasOnCooldown.remove(teamEntry.getKey().getName().toLowerCase());

                        for (Player player : teamEntry.getKey().getOnlineMembers()) {
                            player.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your team is now regenerating DTR!");
                        }
                    }

                    teamEntry.getKey().setDTR(Math.min(teamEntry.getKey().getDTR() + teamEntry.getKey().getDTRIncrement(teamEntry.getValue()), teamEntry.getKey().getMaxDTR()));
                } catch (Exception e) {
                    System.out.println("Error regenerating DTR for team " + teamEntry.getKey().getName() + ".");
                    e.printStackTrace();
                }
            }
        }
    }

}