package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegenCommand {

    @Command(names={ "Regen", "DTR" }, permissionNode="")
    public static void regen(Player sender, @Param(name="target", defaultValue="self") Team target) {
        if (!sender.isOp()) {
            target = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());
        }

        if (target == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (target.getMaxDTR() == target.getDTR()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team is currently at max DTR, which is §d" + target.getMaxDTR() + "§e.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Your team has a max DTR of §d" + target.getMaxDTR() + "§e.");
        sender.sendMessage(ChatColor.YELLOW + "You are regaining DTR at a rate of §d" + Team.DTR_FORMAT.format(target.getDTRIncrement() * 60) + "/hr§e.");
        sender.sendMessage(ChatColor.YELLOW + "At this rate, it will take you §d" + (hrsToRegain(target) == -1 ? "Infinity" : hrsToRegain(target)) + "§eh to fully gain all DTR.");

        if (target.getDTRCooldown() > System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team is on DTR cooldown for §d" + TimeUtils.getDurationBreakdown(target.getDTRCooldown() - System.currentTimeMillis()) + "§e.");
        }
    }

    private static double hrsToRegain(Team team) {
        double cur = team.getDTR();
        double max = team.getMaxDTR();
        double diff = max - cur;
        double dtrIncrement = team.getDTRIncrement();

        if (dtrIncrement == 0D) {
            return (-1);
        }

        double required = diff / dtrIncrement;
        double h = required / 60D;

        return (Math.round(10.0 * h) / 10.0);
    }

}