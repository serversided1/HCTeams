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

        for (ItemStack is : source) {
            if (InventoryUtils.isArmor(is)) {
                is.addUnsafeEnchantment(PROTECTION_ENVIRONMENTAL, PROTECTION_ENVIRONMENTAL.getMaxLevel());
                is.addUnsafeEnchantment(DURABILITY, DURABILITY.getMaxLevel());

                if (InventoryUtils.isBoots(is)) {
                    is.addUnsafeEnchantment(PROTECTION_FALL, PROTECTION_FALL.getMaxLevel());
                }
            } else if (is.getType() == BOW) {
                is.addUnsafeEnchantment(ARROW_DAMAGE, ARROW_DAMAGE.getMaxLevel());
                is.addUnsafeEnchantment(ARROW_FIRE, ARROW_FIRE.getMaxLevel());

                int punchMax = ARROW_KNOCKBACK.getMaxLevel();

                if (punchMax > 0) {
                    is.addUnsafeEnchantment(ARROW_KNOCKBACK, punchMax);
                }

            } else if (is.getType() == DIAMOND_SWORD) {
                is.addUnsafeEnchantment(DAMAGE_ALL, DAMAGE_ALL.getMaxLevel());
                is.addUnsafeEnchantment(DURABILITY, DURABILITY.getMaxLevel());
                is.addUnsafeEnchantment(FIRE_ASPECT, 1);

                ItemMeta meta = is.getItemMeta();
                meta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.RED + ChatColor.ITALIC.toString() + "Koth Fire"); // &b&c&o
                is.setItemMeta(meta); // actually set custom name
            }

            enchanted.add(is);
        }

        // lowest item id first (swords/bows/arrows, then armor from helm to boots)
        Collections.sort(enchanted, (o1, o2) -> Ints.compare(o1.getTypeId(), o2.getTypeId()));

        return enchanted;
    }

    public List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.LIGHT_PURPLE + "Right click to open this " + getKitName() + ChatColor.LIGHT_PURPLE + " crate.");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Crate requires " + ChatColor.GRAY + getSize() + ChatColor.YELLOW + " empty slots to open.");
        return lore;
    }

    public int getSize() {
        return inventory.size();
    }
}
