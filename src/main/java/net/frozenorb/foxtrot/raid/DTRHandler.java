package net.frozenorb.foxtrot.raid;

import java.util.HashSet;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DTRHandler extends BukkitRunnable {

	@Getter private static DTRHandler instance;

	private static final double[] BASE_DTR_INCREMENT = { 1.5, .5, .45, .4, .36,
			.33, .3, .27, .24, .22, .21, .2, .19, .18, .175, .17, .168, .166,
			.164, .162, .16, .158, .156, .154, .152, .15, .148, .146, .144,
			.142 };

	private static HashSet<String> wasOnCooldown = new HashSet<String>();

	public DTRHandler() {
		instance = this;
	}

	public static double getBaseDTRIncrement(int teamsize) {
		return BASE_DTR_INCREMENT[teamsize - 1];
	}

	public static boolean isOnCD(Team team) {
		return wasOnCooldown.contains(team.getFriendlyName().toLowerCase());

	}

	public static boolean isRegenerating(Team team) {
		return !wasOnCooldown.contains(team.getFriendlyName().toLowerCase()) && team.getDtr() != team.getMaxDTR();
	}

	public static void setCooldown(Team team) {
		wasOnCooldown.add(team.getFriendlyName().toLowerCase());
	}

	@Override
	public void run() {
		for (Team t : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {

			if (t.getOnlineMembers().size() > 0) {

				if (t.getDeathCooldown() > System.currentTimeMillis() || t.getRaidableCooldown() > System.currentTimeMillis()) {
					wasOnCooldown.add(t.getFriendlyName().toLowerCase());
					continue;
				}

				if (wasOnCooldown.contains(t.getFriendlyName().toLowerCase())) {
					wasOnCooldown.remove(t.getFriendlyName().toLowerCase());

					for (Player pl : t.getOnlineMembers()) {
						//pl.sendMessage(ChatColor.YELLOW + "Your team is no longer on DTR cooldown and is now regenerating DTR!");
                        pl.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Your team is now regenerating DTR!");
					}
				}

				t.setDtr(Math.min(t.getDtr() + t.getDTRIncrement().doubleValue(), t.getMaxDTR()));

			}
		}
	}
}
