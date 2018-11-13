package net.frozenorb.foxtrot.crates.commands;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.frozenorb.foxtrot.crates.Crate;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;

public class CrateCommand {

    @Command(names = "hctcrate", permission = "op")
    public static void onCreateKit(Player sender, @Param(name = "kit") String kit) {
        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST, 1);
        ItemMeta itemMeta = enderChest.getItemMeta();

        try {
            Crate crate = Crate.valueOf(kit.toUpperCase()); // grab kit from enumeration

            itemMeta.setDisplayName(crate.getKitName());
            itemMeta.setLore(crate.getLore());
            enderChest.setItemMeta(itemMeta);

            sender.getInventory().addItem(enderChest);
            sender.sendMessage(GREEN + "Generated the " + crate.getKitName() + GREEN + " crate and added it to your inventory!");
        } catch (Exception ex) {
            sender.sendMessage(RED + "Cannot create crate item for kit '" + kit + "'");
        }
    }
    
    @Command(names = "hctcrate give", permission = "op")
    public static void onCreateKit(CommandSender sender, @Param(name = "kit") String kit, @Param(name = "target") Player target) {
        ItemStack enderChest = new ItemStack(Material.ENDER_CHEST, 1);
        ItemMeta itemMeta = enderChest.getItemMeta();

        try {
            Crate crate = Crate.valueOf(kit.toUpperCase()); // grab kit from enumeration

            itemMeta.setDisplayName(crate.getKitName());
            itemMeta.setLore(crate.getLore());
            enderChest.setItemMeta(itemMeta);

            target.getInventory().addItem(enderChest);
            sender.sendMessage(GREEN + "Generated the " + crate.getKitName() + GREEN + " crate and added it to " + target.getName() +"'s inventory!");
        } catch (Exception ex) {
            sender.sendMessage(RED + "Cannot create crate item for kit '" + kit + "'");
        }
    }
}
