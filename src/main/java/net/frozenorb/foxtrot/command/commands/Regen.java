package net.frozenorb.foxtrot.command.commands;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.BaseCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;

public class Regen extends BaseCommand {

	public Regen() {
		super("regen", "dtr");
	}

	@Override
	public void syncExecute() {
		final Player p = (Player) sender;

		Team team = FoxtrotPlugin.getInstance().getTeamManager().getPlayerTeam(p.getName());
		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
			return;
		}

		p.sendMessage(ChatColor.YELLOW + "Your team has a max DTR of §d" + team.getMaxDTR() + "§e.");
		p.sendMessage(ChatColor.YELLOW + "You are regaining DTR at a rate of §d" + team.getDTRIncrement().doubleValue() * 60 + "/hr§e.");
		p.sendMessage(ChatColor.YELLOW + "At this rate, it will take you §d" + (hrsToRegain(team) == -1 ? "Infinity" : hrsToRegain(team)) + "§eh to fully gain all DTR.");
		p.sendMessage(ChatColor.YELLOW + "To regain 1 DTR, it would take §d" + (hrTo1(team) == -1 ? "Infinity" : hrTo1(team)) + "h.");

		if (team.getRaidableCooldown() > System.currentTimeMillis() || team.getDeathCooldown() > System.currentTimeMillis()) {

			long till = Math.max(team.getRaidableCooldown(), team.getDeathCooldown());

			p.sendMessage(ChatColor.YELLOW + "Your team is on DTR cooldown for §d" + TimeUtils.getDurationBreakdown(till - System.currentTimeMillis()) + "§e.");
		}

	}

	private double hrsToRegain(Team team) {
		double cur = team.getDtr();
		double max = team.getMaxDTR();

		double diff = max - cur;

		if (team.getDTRIncrement().doubleValue() == 0) {
			return -1;
		}

		double required = diff / team.getDTRIncrement().doubleValue();
		double h = required / 60D;

		return Math.round(10.0 * h) / 10.0;

	}

	private double hrTo1(Team t) {
		double rate = t.getDTRIncrement().doubleValue();
		if (rate == 0) {
			return -1;
		}

		double h = new BigDecimal(Math.round(10.0 * (new BigDecimal("1").divide(new BigDecimal(rate + ""), 5, RoundingMode.HALF_UP)).doubleValue())).divide(new BigDecimal("10"), 5, RoundingMode.HALF_UP).doubleValue() / 60D;
		return Math.round(10.0 * h) / 10.0;

	}
}
