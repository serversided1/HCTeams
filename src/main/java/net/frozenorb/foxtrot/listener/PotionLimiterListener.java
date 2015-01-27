package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.util.NMSMethods;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class PotionLimiterListener implements Listener {

    @EventHandler(priority=EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent event) {
        ItemStack potion = event.getPotion().getItem();

        for (LivingEntity livingEntity : event.getAffectedEntities()) {
            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(livingEntity.getLocation())) {
                event.setIntensity(livingEntity, 0D);
            }
        }

        for (int i : ServerHandler.DISALLOWED_POTIONS) {
            if (i == (int) potion.getDurability()) {
                event.setCancelled(true);
                return;
            }
        }

        if (event.getPotion().getShooter() instanceof Player) {
            Iterator<PotionEffect> iterator = event.getPotion().getEffects().iterator();

            if (iterator.hasNext()) {
                if (Arrays.asList(FoxListener.DEBUFFS).contains(iterator.next().getType())) {
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

        if (ServerHandler.DISALLOWED_POTIONS.contains((int) event.getItem().getDurability())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "This potion is not usable!");
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        InventoryView view = event.getView();

        if (view.getType() != InventoryType.BREWING) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        final BrewerInventory brewerInventory = (BrewerInventory) view.getTopInventory();

        FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

            @Override
            public void run() {
                final ItemStack itemStack = brewerInventory.getIngredient();

                for (int i = 0; i < 3; i++) {
                    if (brewerInventory.getItem(i) == null) {
                        continue;
                    }

                    ItemStack item = brewerInventory.getItem(i);
                    int result = NMSMethods.getPotionResult(item.getDurability(), itemStack);

                    if (item.getType() == Material.AIR || item.getDurability() == result) {
                        continue;
                    }
                }

            }
        }, 1);
    }

}