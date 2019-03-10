package net.frozenorb.foxtrot.map.killstreaks.arcanetypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;

public class Cobwebs extends Killstreak {

    @Override
    public String getName() {
        return "Cobwebs";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                20
        };
    }

    @Override
    public void apply(Player player) {
        give(player, new ItemStack(Material.WEB, 1));
    }

}
