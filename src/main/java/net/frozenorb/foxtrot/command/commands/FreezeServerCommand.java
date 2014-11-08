package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

/**
 * Created by macguy8 on 11/7/2014.
 */
public class FreezeServerCommand {

    public static final String FROZEN_MESSAGE = ChatColor.RED + "The server is currently frozen.";
    private static boolean serverFroze = false;

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onPlayerMove(PlayerMoveEvent event) {
                Player player = event.getPlayer();
                Location from = event.getFrom();
                Location to = event.getTo();

                if (serverFroze && !player.isOp()) {
                    if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                        Location newLoc = from.getBlock().getLocation().add(0.5, 0, 0.5);

                        newLoc.setPitch(to.getPitch());
                        newLoc.setYaw(to.getYaw());
                        event.setTo(newLoc);
                        //player.sendMessage(FROZEN_MESSAGE);
                    }
                }
            }

            @EventHandler
            public void onEntityDamage(EntityDamageEvent event) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();

                    if (serverFroze && !player.isOp()) {
                        event.setCancelled(true);
                        player.sendMessage(FROZEN_MESSAGE);
                    }
                }
            }

            @EventHandler
            public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();

                    if (serverFroze && !damager.isOp()) {
                        event.setCancelled(true);
                        damager.sendMessage(FROZEN_MESSAGE);
                    }
                }
            }

            @EventHandler
            public void onPlayerInteract(PlayerInteractEvent event) {
                if (serverFroze && !event.getPlayer().isOp()) {
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (serverFroze && !event.getWhoClicked().isOp()) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).sendMessage(FROZEN_MESSAGE);
                }
            }

            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                if (serverFroze) {
                    event.getPlayer().sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "The server is currently frozen.");
                }
            }

            @EventHandler
            public void onPlayerTeleport(PlayerTeleportEvent event) {
                if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                    if (serverFroze && !event.getPlayer().isOp()) {
                        event.setCancelled(true);
                        event.setTo(event.getFrom());
                        event.getPlayer().sendMessage(FROZEN_MESSAGE);
                    }
                }
            }

            @EventHandler
            public void onItemDrop(PlayerDropItemEvent event) {
                Player player = event.getPlayer();

                if (serverFroze && !event.getPlayer().isOp()) {
                    event.setCancelled(true);
                    player.updateInventory();
                    player.sendMessage(FROZEN_MESSAGE);
                }
            }

            @EventHandler
            public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
                Player player = event.getPlayer();

                if (serverFroze && !event.getPlayer().isOp()) {
                    event.setCancelled(true);
                    player.updateInventory();
                    player.sendMessage(FROZEN_MESSAGE);
                }
            }

        }, FoxtrotPlugin.getInstance());
    }

    @Command(names={ "freezeserver" }, permissionNode="op")
    public static void freezeServer(Player sender) {
        serverFroze = !serverFroze;
        sender.sendMessage(ChatColor.GREEN + (serverFroze ? "The server is now frozen." : "The server is no longer frozen."));
    }

}