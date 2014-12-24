package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.command.annotations.Param;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chasechocolate.
 */
public class FreezeCommand {

    public static final String FROZEN_MESSAGE = ChatColor.RED + "You may not do this while frozen!";
    private static Set<String> frozen = new HashSet<String>();

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlayerMove(PlayerMoveEvent event) {
                Player player = event.getPlayer();
                Location from = event.getFrom();
                Location to = event.getTo();

                if (isFrozen(player)) {
                    if (from.getX() != to.getX() || from.getZ() != to.getZ()) {
                        Location newLoc = from.getBlock().getLocation().add(0.5, 0, 0.5);

                        newLoc.setPitch(to.getPitch());
                        newLoc.setYaw(to.getYaw());
                        event.setTo(newLoc);
                    }
                }
            }

            @EventHandler
            public void onEntityDamage(EntityDamageEvent event) {
                if (event.getEntity() instanceof Player) {
                    Player player = (Player) event.getEntity();

                    if (isFrozen(player)) {
                        event.setCancelled(true);
                        player.sendMessage(FROZEN_MESSAGE);
                    }
                }
            }

            @EventHandler
            public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
                if (event.getDamager() instanceof Player) {
                    Player damager = (Player) event.getDamager();

                    if (isFrozen(damager)) {
                        event.setCancelled(true);
                        damager.sendMessage(FROZEN_MESSAGE);
                    }
                }
            }

            @EventHandler
            public void onPlayerInteract(PlayerInteractEvent event) {
                if (isFrozen(event.getPlayer())) {
                    event.setCancelled(true);
                }
            }

            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (isFrozen((Player) event.getWhoClicked())) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).sendMessage(FROZEN_MESSAGE);
                }
            }

            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                if (isFrozen(event.getPlayer())) {
                    event.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You have previously been frozen by a staff member, and are still frozen.");
                }
            }

            @EventHandler
            public void onPlayerTeleport(PlayerTeleportEvent event) {
                if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
                    if (isFrozen(event.getPlayer())) {
                        event.setCancelled(true);
                        event.setTo(event.getFrom());
                        event.getPlayer().sendMessage(FROZEN_MESSAGE);
                    }
                }
            }

            @EventHandler
            public void onItemDrop(PlayerDropItemEvent event) {
                Player player = event.getPlayer();

                if (isFrozen(player)) {
                    event.setCancelled(true);
                    player.updateInventory();
                    player.sendMessage(FROZEN_MESSAGE);
                }
            }
        }, FoxtrotPlugin.getInstance());
    }

    @Command(names={ "freeze" }, permissionNode="foxtrot.freeze")
    public static void spawn(Player sender, @Param(name="Params") String argString) {
        String[] args = argString.split(" ");

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /freeze <player | radius>");
            return;
        }

        //Check radius
        try {
            double radius = Double.parseDouble(args[0]);
            Set<Player> freeze = new HashSet<>();

            if (radius > 256) {
                sender.sendMessage(ChatColor.RED + "Too big of a radius! Use /freezeserver for large freeze operations.");
                return;
            }

            for (Entity nearby : ((Player) sender).getNearbyEntities(radius, 256, radius)) {
                if (nearby instanceof Player) {
                    Player p = (Player) nearby;

                    if (!(p.hasMetadata("invisible"))) {
                        freeze.add((Player) nearby);
                    }
                }
            }

            if (freeze.size() == 0) {
                sender.sendMessage(ChatColor.RED + "No nearby players within a " + radius + " radius!");
                return;
            }

            for (Player target : freeze) {
                freeze(target);
            }

            sender.sendMessage(ChatColor.GREEN + "Successfully froze " + freeze.size() + " player" + (freeze.size() == 1 ? "" : "s") + "!");
            return;
        } catch (NumberFormatException e) {
            //Continue
        }

        //Check player
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null || target.hasMetadata("invisible")) {
            sender.sendMessage(ChatColor.RED + "Could not find player '" + args[0] + "'!");
            return;
        }

        if (isFrozen(target)) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is already frozen!");
            return;
        }

        freeze(target);
        sender.sendMessage(ChatColor.GREEN + "You have frozen " + target.getDisplayName() + ChatColor.GREEN + "!");
    }

    public static void freeze(Player player) {
        frozen.add(player.getName());
        player.sendMessage(ChatColor.RED + "You have been frozen by a staff member.");
    }

    public static void unfreeze(Player player) {
        player.sendMessage(ChatColor.GREEN + "You have been unfrozen by a staff member. You may now move and interact.");
        frozen.remove(player.getName());
    }

    public static void unfreezeAll() {
        for (String name : frozen) {
            Player player = Bukkit.getPlayerExact(name);

            if (player != null) {
                unfreeze(player);
            }
        }

        frozen.clear();
    }

    public static boolean isFrozen(Player player) {
        return frozen.contains(player.getName());
    }

}