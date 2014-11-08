package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class PvPTimerListener implements Listener {

    private Set<Integer> droppedItems = new HashSet<Integer>();

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName())) {
            if (droppedItems.contains(event.getItem().getEntityId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        ItemStack it = event.getEntity().getItemStack();

        if (it.hasItemMeta() && it.getItemMeta().hasLore() && it.getItemMeta().getLore().contains("§8PVP Loot")) {
            ItemMeta m = it.getItemMeta();

            List<String> lore = m.getLore();

            lore.remove("§8PVP Loot");
            m.setLore(lore);
            it.setItemMeta(m);

            event.getEntity().setItemStack(it);

            int id = event.getEntity().getEntityId();

            droppedItems.add(id);

            FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

                @Override
                public void run() {
                    droppedItems.remove(id);
                }

            }, 20L * 60);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        for (ItemStack itemStack : event.getDrops()) {
            ItemMeta meta = itemStack.getItemMeta();

            List<String> lore = new ArrayList<String>();

            if (meta.hasLore()) {
                lore = meta.getLore();
            }

            lore.add("§8PVP Loot");
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
    }

}