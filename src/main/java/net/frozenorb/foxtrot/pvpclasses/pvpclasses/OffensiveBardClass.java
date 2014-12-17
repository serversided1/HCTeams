package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class OffensiveBardClass extends BardClass implements Listener {

    public OffensiveBardClass() {
        super("Offensive Bard", "GOLD_");

        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, PotionEffectType.DAMAGE_RESISTANCE.createEffect(20 * 5, 7));
        BARD_CLICK_EFFECTS.put(Material.BLAZE_ROD, PotionEffectType.INCREASE_DAMAGE.createEffect(20 * 3, 0));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, PotionEffectType.JUMP.createEffect(20 * 10, 5));
        BARD_CLICK_EFFECTS.put(Material.RED_MUSHROOM, PotionEffectType.POISON.createEffect(20 * 2, 0));
        BARD_CLICK_EFFECTS.put(Material.BROWN_MUSHROOM, PotionEffectType.WEAKNESS.createEffect(20 * 10, 0));
        BARD_CLICK_EFFECTS.put(Material.SLIME_BALL, PotionEffectType.SLOW.createEffect(20 * 10, 0));
        BARD_CLICK_EFFECTS.put(Material.RAW_FISH, PotionEffectType.WATER_BREATHING.createEffect(20 * 45, 0));
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, PotionEffectType.WITHER.createEffect(140, 0));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, PotionEffectType.SPEED.createEffect(20 * 10, 3));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, PotionEffectType.FIRE_RESISTANCE.createEffect(20 * 45, 0));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, PotionEffectType.REGENERATION.createEffect(20 * 5, 1));

        BARD_CLICK_EFFECTS.put(Material.SPECKLED_MELON, null);
        BARD_CLICK_EFFECTS.put(Material.WHEAT, null);

        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, PotionEffectType.REGENERATION.createEffect(20*6, 0));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, PotionEffectType.FIRE_RESISTANCE.createEffect(20 * 6, 0));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, PotionEffectType.SPEED.createEffect(20*6, 1));
        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, PotionEffectType.DAMAGE_RESISTANCE.createEffect(20*6, 0));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, PotionEffectType.JUMP.createEffect(20*6, 1));
    }

    @Override
    public void giveCustomBardEffect(Player player, Material material) {
        if (material == Material.SPECKLED_MELON) {
            double add = 6.0;
            player.setHealth(Math.min(player.getHealth() + add, player.getMaxHealth()));
        } else if (material == Material.WHEAT) {
            player.setFoodLevel(20);
            player.setSaturation(player.getSaturation() + 14.4F);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        super.onPlayerInteract(event);
    }

}