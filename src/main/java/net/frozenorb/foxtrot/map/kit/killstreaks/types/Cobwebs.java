package net.frozenorb.foxtrot.map.kit.killstreaks.types;

import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cobwebs extends Killstreak {

    @Override
    public String getName() {
        return "Cobwebs";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                12
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.WEB, 32));
    }

}
