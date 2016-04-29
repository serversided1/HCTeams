package net.frozenorb.foxtrot.map.kit.killstreaks.types;

import net.frozenorb.foxtrot.map.kit.killstreaks.Killstreak;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        meta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 6 * 60 * 20, 0), true);
        item.setItemMeta(meta);

        give(player, item);
    }

}
