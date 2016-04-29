package net.frozenorb.foxtrot.map.kit.killstreaks.types;

import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gapple extends Killstreak {

    @Override
    public String getName() {
        return "OP Apple";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                15
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.GOLDEN_APPLE, 1, (byte) 1));
    }

}
