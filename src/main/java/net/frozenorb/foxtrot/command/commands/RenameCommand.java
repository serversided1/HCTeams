package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class RenameCommand {

    @Command(names={ "Rename" }, permissionNode="foxtrot.rename")
    public static void rename(Player sender, @Param(name="Name",wildcard=true) String name) {
        ItemStack inHand = sender.getItemInHand();

        if (inHand == null || inHand.getType() == Material.AIR) {
            sender.sendMessage(ChatColor.RED + "You're not holding anything!");
            return;
        }

        ItemMeta meta = inHand.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        inHand.setItemMeta(meta);
        sender.sendMessage(ChatColor.GRAY + "Renamed your item in hand.");
    }

}