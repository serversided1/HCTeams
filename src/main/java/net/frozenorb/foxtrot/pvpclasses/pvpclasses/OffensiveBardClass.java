package net.frozenorb.foxtrot.pvpclasses.pvpclasses;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OffensiveBardClass extends BaseBardClass implements Listener {

    public OffensiveBardClass() {
        super("Bard", "GOLD_");

        BARD_CLICK_EFFECTS.put(Material.RED_MUSHROOM, new PotionEffect(PotionEffectType.POISON, 20 * 2, 0));
        BARD_CLICK_EFFECTS.put(Material.BROWN_MUSHROOM, new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0));
        BARD_CLICK_EFFECTS.put(Material.SLIME_BALL, new PotionEffect(PotionEffectType.SLOW, 20 * 10, 0));
        BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, new PotionEffect(PotionEffectType.WITHER, 140, 0));
        BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 1));
        BARD_CLICK_EFFECTS.put(Material.SUGAR, new PotionEffect(PotionEffectType.SPEED, 20 * 6, 2));
        BARD_CLICK_EFFECTS.put(Material.FEATHER, new PotionEffect(PotionEffectType.JUMP, 20 * 10, 5));

        BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 6, 0));
        BARD_PASSIVE_EFFECTS.put(Material.SUGAR, new PotionEffect(PotionEffectType.SPEED, 20 * 6, 1));
        BARD_PASSIVE_EFFECTS.put(Material.FEATHER, new PotionEffect(PotionEffectType.JUMP, 20 * 6, 1));
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
            giveBardEffect(player, BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true);
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

}