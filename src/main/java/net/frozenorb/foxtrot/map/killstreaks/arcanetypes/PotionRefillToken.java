package net.frozenorb.foxtrot.map.killstreaks.arcanetypes;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;
import net.frozenorb.qlib.util.ItemBuilder;
import net.minecraft.util.com.google.common.collect.ImmutableList;

public class PotionRefillToken extends Killstreak {

    @Override
    public String getName() {
        return "Potion Refill Token";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                15, 35
        };
    }

    @Override
    public void apply(Player player) {
        give(player, ItemBuilder.of(Material.NETHER_STAR).name("&c&lPotion Refill Token").setUnbreakable(true).setLore(ImmutableList.of("&cRight click this to fill your inventory with potions!")).build());
    }

}