package net.frozenorb.foxtrot.crates.commands;

import net.frozenorb.foxtrot.crates.Crate;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

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
}
