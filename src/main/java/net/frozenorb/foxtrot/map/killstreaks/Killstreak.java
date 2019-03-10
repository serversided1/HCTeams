package net.frozenorb.foxtrot.map.killstreaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public abstract class Killstreak {

    public abstract String getName();

    public abstract int[] getKills();

    public abstract void apply(Player player);

    public boolean check(Player player, int kills) {
        if (shouldApply(kills)) {
            apply(player);
            return true;
        } else {
            return false;
        }
    }

    private boolean shouldApply(int kills) {
        for (int k : getKills()) {
            if (k == kills) {
                return true;
            }
        }

        return false;
    }

    public void give(Player player, ItemStack item) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack current = player.getInventory().getItem(i);

            if (current == null || current.getType() == Material.AIR) {
                player.getInventory().setItem(i, item);
                return;
            }
        }

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack current = player.getInventory().getItem(i);

            if (current != null && current.getType() == Material.POTION) {
                Potion potion = Potion.fromItemStack(current);

                if (potion.getType() == PotionType.INSTANT_HEAL && potion.isSplash()) {
                    player.getInventory().setItem(i, item);
                    return;
                }
            }
        }
    }

}
