package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.bitmask.DTRBitmaskType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Colin on 11/13/2014.
 */
public class BitmaskCommand {

    @Command(names={ "bitmask list", "bitmasks list" }, permissionNode="op")
    public static void bitmaskList(Player sender) {
        for (DTRBitmaskType bitmaskType : DTRBitmaskType.values()) {
            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + "(" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }
    }

    @Command(names={ "bitmask info", "bitmasks info" }, permissionNode="op")
    public static void bitmaskInfo(Player sender, @Param(name="target") Team target) {
        if (target.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        sender.sendMessage(ChatColor.YELLOW + "Bitmask Flags of " + ChatColor.GOLD + target.getFriendlyName() + ChatColor.YELLOW + ":");

        for (DTRBitmaskType bitmaskType : DTRBitmaskType.values()) {
            if (!target.hasDTRBitmask(bitmaskType)) {
                continue;
            }

            sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + "(" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
        }
    }

    @Command(names={ "bitmask add", "bitmasks add" }, permissionNode="op")
    public static void bitmaskAdd(Player sender, @Param(name="target") Team target, @Param(name="bitmask") DTRBitmaskType bitmaskType) {
        if (target.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        target.setDTRBitmask(bitmaskType, true);
        bitmaskInfo(sender, target);
    }

    @Command(names={ "bitmask remove", "bitmasks remove" }, permissionNode="op")
    public static void bitmaskRemove(Player sender, @Param(name="target") Team target, @Param(name="bitmask") DTRBitmaskType bitmaskType) {
        if (target.getOwner() != null) {
            sender.sendMessage(ChatColor.RED + "Bitmask flags cannot be applied to teams without a null leader.");
            return;
        }

        target.setDTRBitmask(bitmaskType, false);
        bitmaskInfo(sender, target);
    }

}