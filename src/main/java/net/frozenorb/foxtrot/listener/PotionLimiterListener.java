package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.Iterator;

public class PotionLimiterListener implements Listener {

    @EventHandler(priority=EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity livingEntity : event.getAffectedEntities()) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(livingEntity.getLocation())) {
                event.setIntensity(livingEntity, 0D);
            }
        }

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            if (!Foxtrot.getInstance().getServerHandler().isSplashPotionAllowed(Potion.fromItemStack(event.getPotion().getItem()).getType())) {
                event.setCancelled(true);
            }
        }

        if (event.getPotion().getShooter() instanceof Player) {
            Iterator<PotionEffect> iterator = event.getPotion().getEffects().iterator();

            if (iterator.hasNext()) {
                if (FoxListener.DEBUFFS.contains(iterator.next().getType())) {
                    if (event.getAffectedEntities().size() > 1 || (event.getAffectedEntities().size() == 1 && !event.getAffectedEntities().contains(event.getPotion().getShooter()))) {
                        SpawnTagHandler.addSeconds((Player) event.getPotion().getShooter(), SpawnTagHandler.MAX_SPAWN_TAG);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) {
            return;
        }

        if (!Foxtrot.getInstance().getServerHandler().isDrinkablePotionAllowed(Potion.fromItemStack(event.getItem()).getType())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "This potion is not usable!");
        }
    }

}