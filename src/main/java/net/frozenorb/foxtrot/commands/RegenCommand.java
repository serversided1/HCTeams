package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Parameter;
import net.frozenorb.qlib.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegenCommand {

    @Command(names={ "Regen", "DTR" }, permissionNode="")
    public static void regen(Player sender, @Parameter(name="target", defaultValue="self") Team target) {
        if (!sender.isOp()) {
            target = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        }

        if (target == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (target.getMaxDTR() == target.getDTR()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team is currently at max DTR, which is " + ChatColor.LIGHT_PURPLE + target.getMaxDTR() + ChatColor.YELLOW + ".");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Your team has a max DTR of " + ChatColor.LIGHT_PURPLE + target.getMaxDTR() + ChatColor.YELLOW + ".");
        sender.sendMessage(ChatColor.YELLOW + "You are regaining DTR at a rate of " + ChatColor.LIGHT_PURPLE + Team.DTR_FORMAT.format(target.getDTRIncrement() * 60) + "/hour" + ChatColor.YELLOW + ".");
        sender.sendMessage(ChatColor.YELLOW + "At this rate, it will take you " + ChatColor.LIGHT_PURPLE + (hrsToRegain(target) == -1 ? "Infinity" : hrsToRegain(target)) + ChatColor.YELLOW + " hours to fully gain all DTR.");

        if (target.getDTRCooldown() > System.currentTimeMillis()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team is on DTR cooldown for " + ChatColor.LIGHT_PURPLE + TimeUtils.formatIntoDetailedString((int) (target.getDTRCooldown() - System.currentTimeMillis()) / 1000) + ChatColor.YELLOW + ".");
        }
    }

    private static double hrsToRegain(Team team) {
        double diff = team.getMaxDTR() - team.getDTR();
        double dtrIncrement = team.getDTRIncrement();

        if (dtrIncrement == 0D) {
            return (-1);
        }

        double required = diff / dtrIncrement;
        double h = required / 60D;

        return (Math.round(10.0 * h) / 10.0);
    }

}