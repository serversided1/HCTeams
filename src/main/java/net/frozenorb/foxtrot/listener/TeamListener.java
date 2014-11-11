package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.factionactiontracker.FactionActionTracker;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
                online.sendMessage(ChatColor.GREEN + "Member Online: " + ChatColor.WHITE + event.getPlayer().getName());
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

        if (FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getBlock().getLocation())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getTeamHandler().isTaken(event.getBlock().getLocation())) {
            Team owner = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getBlock().getLocation());

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

        if (FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getBlock().getLocation())) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getBlock().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + ChatColor.RED + team.getFriendlyName() + ChatColor.YELLOW + "'s territory!");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getBlock().getLocation())) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getBlock().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + ChatColor.RED + team.getFriendlyName() + ChatColor.YELLOW + "'s territory!");
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
        if (event.isCancelled() || !event.isSticky() || FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getBlock().getLocation())) {
            return;
        }

        Block retractBlock = event.getRetractLocation().getBlock();

        if (retractBlock.isEmpty() || retractBlock.isLiquid()) {
            return;
        }

        Team pistonTeam = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getBlock().getLocation());
        Team targetTeam = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(retractBlock.getLocation());

        if (pistonTeam == targetTeam) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (event.isCancelled() || FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getBlock().getLocation())) {
            return;
        }

        Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);
        Team pistonTeam = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getBlock().getLocation());
        Team targetTeam = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(targetBlock.getLocation());

        if (targetTeam == pistonTeam) {
            return;
        }

        if (targetBlock.isEmpty() || targetBlock.isLiquid()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingPlace(HangingPlaceEvent event) {
        if (FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer()) || FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getEntity().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player) || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride((Player) event.getRemover())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getEntity().getLocation());

        if (team != null && !team.isMember((Player) event.getRemover())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.isCancelled() || event.getRightClicked().getType() != EntityType.ITEM_FRAME || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getRightClicked().getLocation())) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getRightClicked().getLocation());

        if (team != null && !team.isMember(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player) || event.getEntity().getType() != EntityType.ITEM_FRAME || FoxtrotPlugin.getInstance().getServerHandler().isAdminOverride((Player) event.getDamager())) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getServerHandler().isClaimedAndRaidable(event.getEntity().getLocation())) {
            return;
        }

        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getEntity().getLocation());

        if (team != null && !team.isMember((Player) event.getDamager())) {
            event.setCancelled(true);
        }
    }

}