package net.frozenorb.foxtrot.map.killstreaks.arcanetypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;

public class GoldenApples extends Killstreak {

    @Override
    public String getName() {
        return "3 Golden Apples";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                3
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 3));
    }

}