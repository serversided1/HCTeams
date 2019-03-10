package net.frozenorb.foxtrot.map.killstreaks.velttypes;

import net.frozenorb.foxtrot.map.killstreaks.PersistentKillstreak;
import net.frozenorb.qlib.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class FireRes extends PersistentKillstreak {

    public FireRes() {
        super("Fire Resistance", 6);
    }

    public void apply(Player player) {
        player.getInventory().addItem(ItemBuilder.of(Material.POTION).data((short) 8227).build());
    }
    
}
