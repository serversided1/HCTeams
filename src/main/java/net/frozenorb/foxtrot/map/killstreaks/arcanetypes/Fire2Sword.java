package net.frozenorb.foxtrot.map.killstreaks.arcanetypes;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;
import net.frozenorb.qlib.util.ItemBuilder;

public class Fire2Sword extends Killstreak {

    @Override
    public String getName() {
        return "Fire II sword";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                100
        };
    }

    @Override
    public void apply(Player player) {
        give(player, ItemBuilder.of(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 1).name("&b&c100 Killstreak Sword").build());
    }

}
