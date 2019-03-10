package net.frozenorb.foxtrot.map.killstreaks.arcanetypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;

public class Gapple extends Killstreak {

    @Override
    public String getName() {
        return "OP Apple";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                40
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1));
    }

}
