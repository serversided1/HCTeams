package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by macguy8 on 11/1/2014.
 */
public class RepairCommand {

    @Command(names={ "Repair" }, permissionNode="foxtrot.repair")
    public static void repair(Player sender) {
        ItemStack inHand = sender.getItemInHand();

        if (inHand == null || inHand.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You're not holding anything!");
            return;
        }

        inHand.setDurability((short) 0);
        sender.sendMessage(ChatColor.GRAY + "Repaired your item in hand.");
    }

}