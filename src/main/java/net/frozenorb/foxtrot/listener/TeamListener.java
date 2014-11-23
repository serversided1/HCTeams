package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class TeamListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

        if (team != null) {
            for (Player online : team.getOnlineMembers()) {
                if (online != event.getPlayer()) {
                    online.sendMessage(ChatColor.GREEN + "Member Online: " + ChatColor.WHITE + event.getPlayer().getName());
                }
            }

            FactionActionTracker.logAction(team, "actions", "Member Online: " + event.getPlayer().getName());
            team.sendTeamInfo(event.getPlayer());
        } else {
            event.getPlayer().sendMessage(ChatColor.GRAY + "You are not on a team!");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(event.getPlayer().getName());

        if (team != null) {
            for (Player online : team.getOnlineMembers()) {
                online.sendMessage(ChatColor.RED + "Member Offline: " + ChatColor.WHITE + event.getPlayer().getName());
            }

            FactionActionTracker.logAction(team, "actions", "Member Offline: " + event.getPlayer().getName());
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getPlayer() != null) {
            if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
                return;
            }
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
            Team owner = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

            if (event.getCause() == BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL && owner.isMember(event.getPlayer())) {
                return;
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + ChatColor.RED + team.getName() + ChatColor.YELLOW + "'s territory!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + ChatColor.RED + team.getName() + ChatColor.YELLOW + "'s territory!");
            event.setCancelled(true);

            if (!Arrays.asList(FoxListener.NON_TRANSPARENT_ATTACK_DISABLING_BLOCKS).contains(event.getBlock().getType())) {
                if (event.getBlock().isEmpty() || event.getBlock().getType().isTransparent() || !event.getBlock().getType().isSolid()) {
                    return;
                }
            }

            FoxtrotPlugin.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (event.isCancelled() || !event.isSticky() || FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        Block retractBlock = event.getRetractLocation().getBlock();

        if (retractBlock.isEmpty() || retractBlock.isLiquid()) {
            return;
        }

        Team pistonTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        Team targetTeam = LandBoard.getInstance().getTeam(retractBlock.getLocation());

        if (pistonTeam == targetTeam) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled() || FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getBlock().getLocation())) {
            return;
        }

        Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);
        Team pistonTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        Team targetTeam = LandBoard.getInstance().getTeam(targetBlock.getLocation());

        if (targetTeam == pistonTeam) {
            return;
        }

        if (targetBlock.isEmpty() || targetBlock.isLiquid()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride((Player) event.getRemover())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (team != null && !team.isMember((Player) event.getRemover())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.isCancelled() || event.getRightClicked().getType() != EntityType.ITEM_FRAME || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getRightClicked().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getRightClicked().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    // Used for item frames
    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player) || event.getEntity().getType() != EntityType.ITEM_FRAME || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride((Player) event.getDamager())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());

        if (team != null && !team.isMember((Player) event.getDamager())) {
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
            Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(damager.getName());
            Player victim = (Player) event.getEntity();

            if (team != null && team.isMember(victim.getName()) && event.getCause() != EntityDamageEvent.DamageCause.FALL) {
                damager.sendMessage(ChatColor.YELLOW + "You cannot hurt " + ChatColor.GREEN + victim.getName() + ChatColor.YELLOW + ".");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        Team owner = LandBoard.getInstance().getTeam(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());

        if (owner != null && !owner.isMember(event.getPlayer())) {
            event.setCancelled(true);
            event.getBlockClicked().getRelative(event.getBlockFace()).setType(Material.AIR);
            event.setItemStack(new ItemStack(event.getBucket()));
            return;
        }
    }

}