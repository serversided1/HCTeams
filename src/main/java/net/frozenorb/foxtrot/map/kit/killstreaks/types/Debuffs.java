package net.frozenorb.foxtrot.map.kit.killstreaks.types;

import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Debuffs extends Killstreak {

    @Override
    public String getName() {
        return "Debuffs";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                6
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.POTION, 1, (byte) 16388));
        give(player, new ItemStack(Material.POTION, 1, (byte) 16421));
    }

}
