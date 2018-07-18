package net.frozenorb.foxtrot.map.kit.killstreaks.velttypes;

import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class StrengthII extends Killstreak {

    @Override
    public String getName() {
        return "Strength II";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                20
        };
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 30 * 20, 1), true);
    }

}
