package net.frozenorb.foxtrot.crates;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.bukkit.Material.*;
import static org.bukkit.enchantments.Enchantment.*;

@AllArgsConstructor
public enum Crate {

    ARCHER(getEnchantedInventory(
            ImmutableSet.of(
                    new ItemStack(LEATHER_HELMET),
                    new ItemStack(LEATHER_CHESTPLATE),
                    new ItemStack(LEATHER_LEGGINGS),
                    new ItemStack(LEATHER_BOOTS),
                    new ItemStack(BOW),
                    new ItemStack(ARROW, 64)
            )
    ),
            "§eArcher Crate"
    ),
    BARD(getEnchantedInventory(
            ImmutableSet.of(
                    new ItemStack(GOLD_HELMET),
                    new ItemStack(GOLD_CHESTPLATE),
                    new ItemStack(GOLD_LEGGINGS),
                    new ItemStack(GOLD_BOOTS)
            )
    )
            ,
            "§6Bard Crate"
    ),
    DIAMOND(getEnchantedInventory(
            ImmutableSet.of(
                    new ItemStack(DIAMOND_HELMET),
                    new ItemStack(DIAMOND_CHESTPLATE),
                    new ItemStack(DIAMOND_LEGGINGS),
                    new ItemStack(DIAMOND_BOOTS),
                    new ItemStack(DIAMOND_SWORD)
            )
    ),
            "§bDiamond Crate"
    );

    @Getter private List<ItemStack> inventory;
    @Getter private String kitName;

    private static List<ItemStack> getEnchantedInventory(Set<ItemStack> source) {
        List<ItemStack> enchanted = new ArrayList<>();

        for (ItemStack itemStack : source) {
            ItemMeta meta = itemStack.getItemMeta();

            if (InventoryUtils.isArmor(itemStack)) {
                meta.addEnchant(PROTECTION_ENVIRONMENTAL, PROTECTION_ENVIRONMENTAL.getMaxLevel(), true);
                meta.addEnchant(DURABILITY, DURABILITY.getMaxLevel(), true);

                if (InventoryUtils.isBoots(itemStack)) {
                    meta.addEnchant(PROTECTION_FALL, PROTECTION_FALL.getMaxLevel(), true);
                }
            } else if (itemStack.getType() == BOW) {
                meta.addEnchant(ARROW_DAMAGE, ARROW_DAMAGE.getMaxLevel(), true);
                meta.addEnchant(ARROW_FIRE, ARROW_FIRE.getMaxLevel(), true);
                meta.addEnchant(ARROW_INFINITE, ARROW_INFINITE.getMaxLevel(), true);
                meta.addEnchant(DURABILITY, 5, true);

                int punchMax = ARROW_KNOCKBACK.getMaxLevel();

                if (punchMax > 0) {
                    meta.addEnchant(ARROW_KNOCKBACK, punchMax, true);
                }

                meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.RED + ChatColor.ITALIC.toString() + "Koth Bow"); // &b&c&o
            } else if (itemStack.getType() == DIAMOND_SWORD) {
                meta.addEnchant(DAMAGE_ALL, DAMAGE_ALL.getMaxLevel(), true);
                meta.addEnchant(DURABILITY, 5, true);
                meta.addEnchant(FIRE_ASPECT, 1, true);

                meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.RED + ChatColor.ITALIC.toString() + "Koth Fire"); // &b&c&o
            }

            itemStack.setItemMeta(meta); // set custom name and enchantments
            enchanted.add(itemStack);
        }

        // lowest item id first (swords/bows/arrows, then armor from helm to boots)
        Collections.sort(enchanted, (o1, o2) -> Ints.compare(o1.getTypeId(), o2.getTypeId()));

        return enchanted;
    }

    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE + "Right click to open this " + getKitName() + ChatColor.DARK_PURPLE + " crate.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Crate requires " + ChatColor.DARK_GRAY + getSize() + ChatColor.YELLOW + " empty slots to open.");
        return lore;
    }

    public int getSize() {
        return inventory.size();
    }
}
