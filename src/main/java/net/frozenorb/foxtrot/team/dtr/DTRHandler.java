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
    private static final double[] MAX_DTR = { 1.01, 2.25, 2.65, 2.85, 3.35, // 1 to 5
                                                                                                                 3.65, 3.85, 4.25, 4.65, 5.15, // 6 to 10
                                                                                                                 5.35, 5.65, 5.85, 6.05, 6.25, // 11 to 15
                                                                                                                 6.35, 6.45, 6.65, 6.85, 7.00, // 15 to 20
                                                                                                                 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7}; // Padding

    private static Set<String> wasOnCooldown = new HashSet<String>();

    // * 4.5 is to 'speed up' DTR regen while keeping the ratios the same.
    // We're using this instead of changing the array incase we need to change this value
    // In the future.
    public static double getBaseDTRIncrement(int teamsize) {
        return (teamsize == 0 ? 0 : BASE_DTR_INCREMENT[teamsize - 1] * 4.5F);
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
                    FoxtrotPlugin.getInstance().getBugSnag().notify(e);
                    e.printStackTrace();
                }
            }
        }
    }

}