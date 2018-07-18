package net.frozenorb.foxtrot.map.kit.killstreaks.velttypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import net.frozenorb.qlib.util.ItemBuilder;

public class FireRes extends Killstreak {

    @Override
    public String getName() {
        return "Fire Resistance";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                12
        };
    }

    @Override
    public void apply(Player player) {
        give(player, ItemBuilder.of(Material.POTION).data((short) 8259).build());
    }

}
