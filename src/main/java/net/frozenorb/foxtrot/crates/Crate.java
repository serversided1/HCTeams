package net.frozenorb.foxtrot.crates;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.Material.*;

@AllArgsConstructor
enum Crate {

    ARCHER(
            ImmutableSet.of(
                    new ItemStack(LEATHER_HELMET),
                    new ItemStack(LEATHER_CHESTPLATE),
                    new ItemStack(LEATHER_LEGGINGS),
                    new ItemStack(LEATHER_BOOTS),
                    new ItemStack(BOW),
                    new ItemStack(ARROW, 64)
            ),
            "§eArcher Crate"
    ),
    BARD(
            ImmutableSet.of(
                    new ItemStack(GOLD_HELMET),
                    new ItemStack(GOLD_CHESTPLATE),
                    new ItemStack(GOLD_LEGGINGS),
                    new ItemStack(GOLD_BOOTS)
            ),
            "§6Bard Crate"
    ),
    DIAMOND(
            ImmutableSet.of(
                    new ItemStack(DIAMOND_HELMET),
                    new ItemStack(DIAMOND_CHESTPLATE),
                    new ItemStack(DIAMOND_LEGGINGS),
                    new ItemStack(DIAMOND_BOOTS),
                    new ItemStack(DIAMOND_SWORD)
            ),
            "§bDiamond Crate"
    );

    private Set<ItemStack> unzip;
    @Getter private String kitName;

    public Set<ItemStack> getEnchantedInventory() {
        Set<ItemStack> enchanted = new HashSet<>();

        for(ItemStack is : unzip) {
            if (InventoryUtils.isArmor(is)) {
                is.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel());
                is.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());

                if(InventoryUtils.isBoots(is)) {
                    is.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, Enchantment.PROTECTION_FALL.getMaxLevel());
                }
            } else if (is.getType() == BOW) {
                is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, Enchantment.ARROW_DAMAGE.getMaxLevel());
                is.addUnsafeEnchantment(Enchantment.ARROW_FIRE, Enchantment.ARROW_FIRE.getMaxLevel());

                int punchMax = Enchantment.ARROW_KNOCKBACK.getMaxLevel();

                if (punchMax > 0) {
                    is.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, Enchantment.ARROW_KNOCKBACK.getMaxLevel());
                }

            } else if (is.getType() == DIAMOND_SWORD) {
                is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_ALL.getMaxLevel());
                is.addUnsafeEnchantment(Enchantment.DURABILITY, Enchantment.DURABILITY.getMaxLevel());
                is.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
            }

            enchanted.add(is);
        }

        return enchanted;
    }

    public int getSize() {
        return unzip.size();
    }
}
