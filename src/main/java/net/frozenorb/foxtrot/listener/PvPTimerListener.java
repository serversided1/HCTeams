package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
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

    private Set<Integer> droppedItems = new HashSet<>();

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
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

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> droppedItems.remove(id), 20L * 60);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        for (ItemStack itemStack : event.getDrops()) {
            ItemMeta meta = itemStack.getItemMeta();

            List<String> lore = new ArrayList<>();

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

            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
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

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
            damager.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
            damager.sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
            event.setCancelled(true);
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId())) {
            damager.sendMessage(ChatColor.RED + "That player currently has their PVP Timer!");
            event.setCancelled(true);
        }
    }

}