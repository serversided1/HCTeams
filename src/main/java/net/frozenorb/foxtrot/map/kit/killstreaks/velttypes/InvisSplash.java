package net.frozenorb.foxtrot.map.kit.killstreaks.velttypes;

import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class InvisSplash extends Killstreak {

    @Override
    public String getName() {
        return "Invis Splash";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                10
        };
    }

    @Override
    public void apply(Player player) {
        Potion potion = new Potion(PotionType.INVISIBILITY, 1);
        potion.setSplash(true);

        ItemStack item = potion.toItemStack(1);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        meta.setMainEffect(PotionEffectType.INVISIBILITY);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6 * 60 * 20, 0), true);
        item.setItemMeta(meta);

        give(player, item);
    }

}
