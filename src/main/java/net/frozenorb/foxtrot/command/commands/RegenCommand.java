package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RegenCommand {

    @Command(names={ "Regen", "DTR" }, permissionNode="")
    public static void regen(Player sender) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMaxDTR() == team.getDtr()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team is currently at max DTR, which is §d" + team.getMaxDTR() + "§e.");
            return;
        }


        sender.sendMessage(ChatColor.YELLOW + "Your team has a max DTR of §d" + team.getMaxDTR() + "§e.");
        sender.sendMessage(ChatColor.YELLOW + "You are regaining DTR at a rate of §d" + Team.DTR_FORMAT.format(team.getDTRIncrement().doubleValue() * 60) + "/hr§e.");
        sender.sendMessage(ChatColor.YELLOW + "At this rate, it will take you §d" + (hrsToRegain(team) == -1 ? "Infinity" : hrsToRegain(team)) + "§eh to fully gain all DTR.");
        sender.sendMessage(ChatColor.YELLOW + "To regain 1 DTR, it would take §d" + (hrTo1(team) == -1 ? "Infinity" : hrTo1(team)) + "h.");

        if (team.getRaidableCooldown() > System.currentTimeMillis() || team.getDeathCooldown() > System.currentTimeMillis()) {
            long till = Math.max(team.getRaidableCooldown(), team.getDeathCooldown());
            sender.sendMessage(ChatColor.YELLOW + "Your team is on DTR cooldown for §d" + TimeUtils.getDurationBreakdown(till - System.currentTimeMillis()) + "§e.");
        }

    }

    private static double hrsToRegain(Team team) {
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

    private static double hrTo1(Team t) {
        double rate = t.getDTRIncrement().doubleValue();
        if (rate == 0) {
            return -1;
        }

        double h = new BigDecimal(Math.round(10.0 * (new BigDecimal("1").divide(new BigDecimal(rate + ""), 5, RoundingMode.HALF_UP)).doubleValue())).divide(new BigDecimal("10"), 5, RoundingMode.HALF_UP).doubleValue() / 60D;
        return Math.round(10.0 * h) / 10.0;
    }

}