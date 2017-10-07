package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class BitmaskCommand {

    //TODO: Cleanup

    @Command(names={ "bitmask list", "bitmasks list" }, permission="op")
    public static void bitmaskList(Player sender) {
        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }
    }

    @Command(names={ "bitmask info", "bitmasks info" }, permission="op")
    public static void bitmaskInfo(Player sender, @Param(name="team") Team team) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Bitmask flags of " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + ":");

        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (!team.hasDTRBitmask(bitmaskType)) {
                continue;
            }

            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }

        sender.sendMessage(ChatColor.GOLD + "Raw DTR: " + ChatColor.YELLOW + team.getDTR());
    }

    @Command(names={ "bitmask add", "bitmasks add" }, permission="op")
    public static void bitmaskAdd(Player sender, @Param(name="target") Team team, @Param(name="bitmask") DTRBitmask bitmask) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        if (team.hasDTRBitmask(bitmask)) {
            sender.sendMessage(ChatColor.RED + "This claim already has the bitmask value " + bitmask.getName() + ".");
            return;
        }

        int dtrInt = (int) team.getDTR();

        dtrInt += bitmask.getBitmask();

        team.setDTR(dtrInt);
        bitmaskInfo(sender, team);
    }

    @Command(names={ "bitmask remove", "bitmasks remove" }, permission="op")
    public static void bitmaskRemove(Player sender, @Param(name="team") Team team, @Param(name="bitmask") DTRBitmask bitmask) {
        if (team.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        if (!team.hasDTRBitmask(bitmask)) {
            sender.sendMessage(ChatColor.RED + "This claim doesn't have the bitmask value " + bitmask.getName() + ".");
            return;
        }

        int dtrInt = (int) team.getDTR();

        dtrInt -= bitmask.getBitmask();

        team.setDTR(dtrInt);
        bitmaskInfo(sender, team);
    }

}