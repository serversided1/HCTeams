package net.frozenorb.foxtrot.raid;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class DTRHandler extends BukkitRunnable {

	@Getter private static DTRHandler instance;

	private static final double[] BASE_DTR_INCREMENT = { 1.5, .5, .45, .4, .36,
			.33, .3, .27, .24, .22, .21, .2, .19, .18, .175, .17, .168, .166,
			.164, .162, .16, .158, .156, .154, .152, .15, .148, .146, .144,
			.142 };
    private static final double[] MAX_DTR = { 1.01, 1.8, 2.2, 2.7, 3.2,
            3.4, 3.6, 3.8, 3.9, 4.18, 4.23, 4.36, 4.42, 4.59, 4.67, 4.72, 4.89, 4.92,
            5.04, 5.15, 5.29, 5.37, 5.48, 5.52, 5.6, 5.73, 5.81, 5.96, 6.08,
            6.16 };

	private static Set<String> wasOnCooldown = new HashSet<String>();

	public DTRHandler() {
		instance = this;
	}

	public static double getBaseDTRIncrement(int teamsize) {
		return (BASE_DTR_INCREMENT[teamsize - 1]);
	}

    public static double getMaxDTR(int teamsize) {
        return (MAX_DTR[teamsize - 1]);
    }

	public static boolean isOnCD(Team team) {
		return (wasOnCooldown.contains(team.getFriendlyName().toLowerCase()));
	}

	public static boolean isRegenerating(Team team) {
		return (!wasOnCooldown.contains(team.getFriendlyName().toLowerCase()) && team.getDtr() != team.getMaxDTR());
	}

	public static void setCooldown(Team team) {
		wasOnCooldown.add(team.getFriendlyName().toLowerCase());
	}

	@Override
	public void run() {
        Set<Team> recentlyTicked = new HashSet<Team>();

        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            Team playerTeam = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());

            if (playerTeam != null && !recentlyTicked.contains(playerTeam)) {
                recentlyTicked.add(playerTeam);

                if (playerTeam.getOwner() != null && !playerTeam.getOwner().equalsIgnoreCase("null")) {
                    if (playerTeam.getDeathCooldown() > System.currentTimeMillis() || playerTeam.getRaidableCooldown() > System.currentTimeMillis()) {
                        wasOnCooldown.add(playerTeam.getFriendlyName().toLowerCase());
                        continue;
                    }

                    if (wasOnCooldown.contains(playerTeam.getFriendlyName().toLowerCase())) {
                        wasOnCooldown.remove(playerTeam.getFriendlyName().toLowerCase());

                        for (Player pl : playerTeam.getOnlineMembers()) {
                            pl.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your team is now regenerating DTR!");
                        }
                    }

                    playerTeam.setDtr(Math.min(playerTeam.getDtr() + playerTeam.getDTRIncrement().doubleValue(), playerTeam.getMaxDTR()));
                }
            }
        }
	}

}