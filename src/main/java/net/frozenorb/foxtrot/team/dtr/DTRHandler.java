package net.frozenorb.foxtrot.team.dtr;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DTRHandler extends BukkitRunnable {

    private static double[] BASE_DTR_INCREMENT = { 1.5, .5, .45, .4, .36,
            .33, .3, .27, .24, .22, .21, .2, .19, .18, .175, .17, .168, .166,
            .164, .162, .16, .158, .156, .154, .152, .15, .148, .146, .144,
            .142, .142, .142, .142, .142, .142,
            .142, .142, .142, .142, .142 };
    private static double[] MAX_DTR = { 1.01, 2.25, 2.65, 2.85, 3.35, // 1 to 5
            3.65, 3.85, 4.25, 4.65, 5.15, // 6 to 10
            5.35, 5.65, 5.85, 6.05, 6.25, // 11 to 15
            6.35, 6.45, 6.65, 6.85, 7.00, // 15 to 20

            7, 7, 7, 7, 7,
            7, 7, 7, 7, 7,
            7, 7, 7, 7, 7,
            7, 7, 7, 7, 7 }; // Padding

    private static Set<String> wasOnCooldown = new HashSet<>();

    public static void loadDTR() {
        FileConfiguration configuration = Foxtrot.getInstance().getConfig();

        List<Double> baseIncrement = configuration.getDoubleList("DTR.BaseDTRIncrement");
        List<Double> maxDTR = configuration.getDoubleList("DTR.Max");

        if (baseIncrement != null && !baseIncrement.isEmpty()) {
            double[] doubles = new double[baseIncrement.size()];

            for (int i = 0; i < baseIncrement.size(); i++) {
                doubles[i] = baseIncrement.get(i);
            }

            BASE_DTR_INCREMENT = doubles;
        } else {
            configuration.set("DTR.BaseDTRIncrement", Arrays.asList(BASE_DTR_INCREMENT));
            Foxtrot.getInstance().saveConfig();
        }

        if (maxDTR != null && !maxDTR.isEmpty()) {
            double[] doubles = new double[maxDTR.size()];

            for (int i = 0; i < maxDTR.size(); i++) {
                doubles[i] = maxDTR.get(i);
            }

            BASE_DTR_INCREMENT = doubles;
        } else {
            configuration.set("DTR.Max", Arrays.asList(MAX_DTR));
            Foxtrot.getInstance().saveConfig();
        }
    }

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
        Map<Team, Integer> playerOnlineMap = new HashMap<>();

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (player.hasMetadata("invisible")) {
                continue;
            }

            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

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
                        teamEntry.getKey().sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your team is now regenerating DTR!");
                    }

                    teamEntry.getKey().setDTR(Math.min(teamEntry.getKey().getDTR() + teamEntry.getKey().getDTRIncrement(teamEntry.getValue()), teamEntry.getKey().getMaxDTR()));
                } catch (Exception e) {
                    Foxtrot.getInstance().getLogger().warning("Error regenerating DTR for team " + teamEntry.getKey().getName() + ".");
                    e.printStackTrace();
                }
            }
        }
    }

}