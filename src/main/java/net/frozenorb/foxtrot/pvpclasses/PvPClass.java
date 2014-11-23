package net.frozenorb.foxtrot.pvpclasses;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public abstract class PvPClass implements Listener {

    @Getter String name;
    @Getter int warmup;
    @Getter String armorContains;
    @Getter List<Material> consumables;

    public PvPClass(String name, int warmup, String armorContains, List<Material> consumables) {
        this.name = name;
        this.warmup = warmup;
        this.armorContains = armorContains;
        this.consumables = consumables;
    }

    public void apply(Player player) {

    }

    public void tick(Player player) {

    }

    public void remove(Player player) {

    }

    public void removeInfiniteEffects(Player player) {
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

    public static void smartAddPotion(Player player, PotionEffect potionEffect) {
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            if (activePotionEffect.getType().equals(potionEffect.getType())) {
                 if (potionEffect.getAmplifier() < activePotionEffect.getAmplifier()) {
                    return;
                }

                if (potionEffect.getAmplifier() == activePotionEffect.getAmplifier() && potionEffect.getDuration() < activePotionEffect.getDuration()) {
                    return;
                }

                break;
            }
        }

        player.removePotionEffect(potionEffect.getType());
        player.addPotionEffect(potionEffect);
    }

}