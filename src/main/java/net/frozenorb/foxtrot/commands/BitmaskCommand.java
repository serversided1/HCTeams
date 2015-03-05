package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.qlib.command.annotations.Command;
import net.frozenorb.qlib.command.annotations.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BitmaskCommand {

    //TODO: Cleanup

    @Command(names={ "bitmask list", "bitmasks list" }, permissionNode="op")
    public static void bitmaskList(Player sender) {
        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }
    }

    @Command(names={ "bitmask info", "bitmasks info" }, permissionNode="op")
    public static void bitmaskInfo(Player sender, @Parameter(name="target") Team target) {
        if (target.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Bitmask flags of " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + ":");

        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (!target.hasDTRBitmask(bitmaskType)) {
                continue;
            }

            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }

        sender.sendMessage(ChatColor.GOLD + "Raw DTR: " + ChatColor.YELLOW + target.getDTR());
    }

    @Command(names={ "bitmask add", "bitmasks add" }, permissionNode="op")
    public static void bitmaskAdd(Player sender, @Parameter(name="target") Team target, @Parameter(name="bitmask") DTRBitmask bitmaskType) {
        if (target.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        if (target.hasDTRBitmask(bitmaskType)) {
            sender.sendMessage(ChatColor.RED + "This claim already has the bitmask value " + bitmaskType.getName() + ".");
            return;
        }

        int dtrInt = (int) target.getDTR();

        dtrInt += bitmaskType.getBitmask();

        target.setDTR(dtrInt);
        bitmaskInfo(sender, target);
    }

    @Command(names={ "bitmask remove", "bitmasks remove" }, permissionNode="op")
    public static void bitmaskRemove(Player sender, @Parameter(name="target") Team target, @Parameter(name="bitmask") DTRBitmask bitmaskType) {
        if (target.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        if (!target.hasDTRBitmask(bitmaskType)) {
            sender.sendMessage(ChatColor.RED + "This claim doesn't have the bitmask value " + bitmaskType.getName() + ".");
            return;
        }

        int dtrInt = (int) target.getDTR();

        dtrInt -= bitmaskType.getBitmask();

        target.setDTR(dtrInt);
        bitmaskInfo(sender, target);
    }

}