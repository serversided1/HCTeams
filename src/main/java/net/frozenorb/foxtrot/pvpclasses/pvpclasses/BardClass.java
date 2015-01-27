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

public class BardClass extends BaseBardClass implements Listener {

    public BardClass() {
        super("Bard", "GOLD_");

        // Click buffs
        BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, new BardEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1), 45));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, new BardEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2), 25));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, new BardEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 6), 25));
        BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, new BardEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, new BardEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 2), 40));
        BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, new BardEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 45, 0), 40));

        // Click debuffs
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, new BardEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 25));

        // Passive buffs
        BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, new BardEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, new BardEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, new BardEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 6, 1)));
        BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, new BardEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, new BardEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 6, 0)));
        BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, new BardEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 6, 0)));
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
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
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
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