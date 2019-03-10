package net.frozenorb.foxtrot.map.killstreaks.arcanetypes;

import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import net.frozenorb.foxtrot.map.killstreaks.Killstreak;

public class Debuffs extends Killstreak {

    @Override
    public String getName() {
        return "Debuffs";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                7
        };
    }

    @Override
    public void apply(Player player) {
        Potion poison = new Potion(PotionType.POISON);
        poison.setSplash(true);

        Potion slowness = new Potion(PotionType.SLOWNESS);
        slowness.setSplash(true);

        give(player, poison.toItemStack(1));
        give(player, slowness.toItemStack(1));
    }

}
