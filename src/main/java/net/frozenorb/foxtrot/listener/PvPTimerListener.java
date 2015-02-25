package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        ItemStack itemStack = event.getEntity().getItemStack();

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore() && itemStack.getItemMeta().getLore().contains("§8PVP Loot")) {
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();

            lore.remove("§8PVP Loot");
            meta.setLore(lore);
            itemStack.setItemMeta(meta);

            event.getEntity().setItemStack(itemStack);

            final int id = event.getEntity().getEntityId();

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

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(player.getName())) {
                player.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                player.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager == null) {
            return;
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(damager.getName())) {
            damager.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            damager.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setCancelled(true);
            return;
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isPreEOTW() && FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(((Player) event.getEntity()).getName())) {
            damager.sendMessage(ChatColor.RED + "That player currently has their PVP Timer!");
            event.setCancelled(true);
        }
    }

}