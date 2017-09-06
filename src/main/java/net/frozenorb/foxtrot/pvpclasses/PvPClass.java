package net.frozenorb.foxtrot.pvpclasses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public abstract class PvPClass implements Listener {

    @Getter String name;
    @Getter String siteLink;
    @Getter int warmup;
    @Getter String armorContains;
    @Getter List<Material> consumables;

    public PvPClass(String name, int warmup, String armorContains, List<Material> consumables) {
        this.name = name;
        this.siteLink = "www." + Foxtrot.getInstance().getServerHandler().getNetworkWebsite() + "/" + name.toLowerCase().replaceAll(" ", "-");
        this.warmup = warmup;
        this.armorContains = armorContains;
        this.consumables = consumables;

        // Reduce warmup on kit maps
        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isVeltKitMap()) {
            this.warmup = 5;
        }
    }

    public void apply(Player player) {

    }

    public void tick(Player player) {

    }

    public void remove(Player player) {

    }

    public boolean canApply(Player player) {
        return (true);
    }

    public static void removeInfiniteEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1_000_000) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public boolean itemConsumed(Player player, Material type) {
        return (true);
    }

    public boolean qualifies(PlayerInventory armor) {
        return (armor.getHelmet() != null && armor.getChestplate() != null && armor.getLeggings() != null && armor.getBoots() != null &&
                       armor.getHelmet().getType().name().startsWith(armorContains) && armor.getChestplate().getType().name().startsWith(armorContains) && armor.getLeggings().getType().name().startsWith(armorContains) && armor.getBoots().getType().name().startsWith(armorContains));
    }

    public static void smartAddPotion(final Player player, PotionEffect potionEffect, boolean persistOldValues, PvPClass pvpClass) {
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            if (!activePotionEffect.getType().equals(potionEffect.getType())) {
                continue;
            }

            // We're not going to apply anything if they already have a higher tiered potion effect
            if (activePotionEffect.getAmplifier() > potionEffect.getAmplifier()) {
                return;
            }

            // If we have the exact same potion except for the durations...
            if (potionEffect.getAmplifier() == activePotionEffect.getAmplifier()) {
                // If their potion effect is 'better', don't apply ours.
                if (activePotionEffect.getDuration() > potionEffect.getDuration()) {
                    return;
                }

                // If the durations are pretty much the same we return (to avoid spamming them with potion effects)
                if (Math.abs(activePotionEffect.getDuration() - potionEffect.getDuration()) < 20) {
                    return;
                }
            }

            break;
        }

        // CUSTOM
        if (potionEffect.getType().equals(PotionEffectType.SPEED) && persistOldValues) {
            for (final PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (!activePotionEffect.getType().equals(potionEffect.getType())) {
                    continue;
                }

                PvPClassHandler.getSavedPotions().put( player.getUniqueId(), new SavedPotion(activePotionEffect, (System.currentTimeMillis() + ((potionEffect.getDuration() ) * 50) + 50), activePotionEffect.getDuration() >= 1_000_000L));
            }
        }

        player.addPotionEffect(potionEffect, true);
    }

    @AllArgsConstructor
    public static class SavedPotion {

        @Getter PotionEffect potionEffect;
        @Getter long time;
        @Getter private boolean perm;

    }

}
