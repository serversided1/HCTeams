package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class  SpawnListener implements Listener {

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
                return;
            }
        }

        if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in spawn!");
        } else if (FoxtrotPlugin.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation()) || FoxtrotPlugin.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in spawn!");
        } else if (FoxtrotPlugin.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation()) || FoxtrotPlugin.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation())) {
            event.setCancelled(true);

            if (event.getBlock().getType() != Material.LONG_GRASS && event.getBlock().getType() == Material.GRASS) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride((Player) event.getRemover())) {
            return;
        }

        if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.isCancelled() || event.getRightClicked().getType() != EntityType.ITEM_FRAME || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
        }
    }

    // Used for item frames
    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player) || event.getEntity().getType() != EntityType.ITEM_FRAME || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride((Player) event.getDamager())) {
            return;
        }

        if (DTRBitmaskType.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isSpawnBufferZone(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getBlockClicked().getRelative(event.getBlockFace()).setType(Material.AIR);
            event.setItemStack(new ItemStack(event.getBucket()));
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled() || FoxtrotPlugin.getInstance().getServerHandler().isEOTW()) {
            return;
        }

        if ((event.getEntity() instanceof Player || event.getEntity() instanceof Horse) && DTRBitmaskType.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
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

        if (damager != null) {
            Player victim = (Player) event.getEntity();

            if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && (DTRBitmaskType.SAFE_ZONE.appliesAt(victim.getLocation()) || DTRBitmaskType.SAFE_ZONE.appliesAt(damager.getLocation()))) {
                event.setCancelled(true);
            }
        }
    }

}