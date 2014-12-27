package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.bard.BardEffect;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HealerClass extends BaseBardClass implements Listener {

    public HealerClass() {
        super("Healer", "IRON_");

        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, new BardEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 1), 40));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, new BardEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 1), 40));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, new BardEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), 40));

        BARD_CLICK_EFFECTS.put(Material.SPECKLED_MELON, new BardEffect(null, 40, "Heals 3 hearts"));

        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, new BardEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, new BardEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, new BardEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0)));
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
    }

    @Override
    public void tick(Player player) {
        if (player.getItemInHand() != null && BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType()) && (FoxtrotPlugin.getInstance().getServerHandler().isEOTW() || !DTRBitmaskType.SAFE_ZONE.appliesAt(player.getLocation()))) {
            giveBardEffect(player, BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false);
        }

        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
        }
    }

    @Override
    public void giveCustomBardEffect(Player player, Material material) {
        if (material == Material.SPECKLED_MELON) {
            double add = 6.0;
            player.setHealth(Math.min(player.getHealth() + add, player.getMaxHealth()));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        super.onPlayerInteract(event);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        super.onPlayerItemHeld(event);
    }

}