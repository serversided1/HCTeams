package net.frozenorb.foxtrot.nametag.listener;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.server.SpawnTag;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class EnderpearlListener implements Listener {

    @Getter
    private static Map<String, Long> enderpearlCooldown = new HashMap<String, Long>();

    @EventHandler(priority=EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled() || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) event.getEntity().getShooter();

        if (event.getEntity() instanceof EnderPearl) {
            Team ownerTo = FoxtrotPlugin.getInstance().getTeamHandler().getOwner(event.getEntity().getLocation());

            if (ownerTo == null || ownerTo.getDtr() != 100D) {
                enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + 16000L);
            } else {
                enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + 60000L);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || !(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) || event.getItem().getType() != Material.ENDER_PEARL) {
            return;
        }

        if (enderpearlCooldown.containsKey(event.getPlayer().getName()) && enderpearlCooldown.get(event.getPlayer().getName()) > System.currentTimeMillis()) {
            long millisLeft = enderpearlCooldown.get(event.getPlayer().getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = Math.round(10.0 * value) / 10.0;

            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            event.getPlayer().updateInventory();
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled() || event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            return;
        }

        Location target = event.getTo();
        Location from = event.getFrom();

        if (FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(target)) {
            if (!FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(from)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into spawn!");
                return;
            }
        }

        if (!FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(target) || !FoxtrotPlugin.getInstance().getServerHandler().isGlobalSpawn(from)) {
            if (event.getPlayer().getWorld().getEnvironment() != World.Environment.THE_END) {
                SpawnTag.addSeconds(event.getPlayer(), 8);
            }
        }

        Material mat = event.getTo().getBlock().getType();

        if (((mat == Material.THIN_GLASS || mat == Material.IRON_FENCE) && clippingThrough(target, from, 0.65)) || ((mat == Material.FENCE || mat == Material.NETHER_FENCE) && clippingThrough(target, from, 0.45))) {
            event.setTo(from);
            return;
        }

        target.setX(target.getBlockX() + 0.5);
        target.setZ(target.getBlockZ() + 0.5);
        event.setTo(target);
    }

    public boolean clippingThrough(Location target, Location from, double thickness) {
        return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) || (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
    }

}