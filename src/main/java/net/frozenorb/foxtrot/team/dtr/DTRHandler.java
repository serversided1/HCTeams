package net.frozenorb.foxtrot.team.dtr;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DTRHandler extends BukkitRunnable {

    private static double[] BASE_DTR_INCREMENT = { 1.5, .5, .45, .4, .36,
            .33, .3, .27, .24, .22, .21, .2, .19, .18, .175, .17, .168, .166,
            .164, .162, .16, .158, .156, .154, .152, .15, .148, .146, .144,
            .142, .142, .142, .142, .142, .142,
            .142, .142, .142, .142, .142 };
    private static double[] MAX_DTR = { 1.01, 2.01, 3.25, 3.75, 4.50, // 1 to 5
            5.25, 5.50, 5.50, 5.50, // 6 to 10
            5.50, 5.50, 5.50, 5.80, 6.05, // 11 to 15
            6.15, 6.25, 6.35, 6.45, 6.55, // 16 to 20

            6.65, 6.7, 6.85, 6.95, 7.00, // 21 to 25
            7.0, 7.0, 7.0, 7.0, 7.0, // 26 to 30
            7.0, 7.0, 7.0, 7.0, 7.0, // 31 to 35
            9, 9, 9, 9, 9 }; // Padding

    private static Set<ObjectId> wasOnCooldown = new HashSet<>();

    public static void loadDTR() {
        if (Foxtrot.getInstance().getServerHandler().isSquads()) {
            MAX_DTR = new double[] {
                    1.01, 1.44, 1.80, 2.20, // 1 to 4
                    2.60, 3.00, 3.40, 3.80, // 5 to 8

                    4.10, 4.20, 4.20, 4.20, // 9 to 10
                    4.20, 4.20, 4.20, 4.20,
                    4.20, 4.20, 4.20, 4.20,
                    4.20, 4.20, 4.20, 4.20,
                    4.20, 4.20, 4.20, 4.20 }; // Padding
        }
    }

    // * 4.5 is to 'speed up' DTR regen while keeping the ratios the same.
    // We're using this instead of changing the array incase we need to change this value
    // In the future.
    public static double getBaseDTRIncrement(int teamsize) {
        return (teamsize == 0 ? 0 : BASE_DTR_INCREMENT[teamsize - 1] * Foxtrot.getInstance().getMapHandler().getDtrIncrementMultiplier());
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

    public static void markOnDTRCooldown(Team team) {
        wasOnCooldown.add(team.getUniqueId());
    }

    @Override
    public void run() {
        Map<Team, Integer> playerOnlineMap = new HashMap<>();

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (player.hasMetadata("invisible")) {
                continue;
            }

            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

            if (playerTeam != null && playerTeam.getOwner() != null) {
                playerOnlineMap.put(playerTeam, playerOnlineMap.getOrDefault(playerTeam, 0) + 1);
            }
        }

        playerOnlineMap.forEach((team, onlineCount) -> {
            try {
                // make sure (I guess?)
                if (isOnCooldown(team)) {
                    markOnDTRCooldown(team);
                    return;
                }

                if (wasOnCooldown.remove(team.getUniqueId())) {
                    team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your team is now regenerating DTR!");
                }

                double incrementedDtr = team.getDTR() + team.getDTRIncrement(onlineCount);
                double maxDtr = team.getMaxDTR();
                double newDtr = Math.min(incrementedDtr, maxDtr);
                team.setDTR(newDtr);
            } catch (Exception ex) {
                Foxtrot.getInstance().getLogger().warning("Error regenerating DTR for team " + team.getName() + ".");
                ex.printStackTrace();
            }
        });
    }

}
