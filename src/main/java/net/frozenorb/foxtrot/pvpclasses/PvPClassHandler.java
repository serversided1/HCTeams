package net.frozenorb.foxtrot.pvpclasses;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.DefensiveBardClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.OffensiveBardClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class PvPClassHandler extends BukkitRunnable implements Listener {

    @Getter private static Map<String, KitTask> warmupTasks = new HashMap<String, KitTask>();
    @Getter private static Map<String, PvPClass> equippedKits = new HashMap<String, PvPClass>();

    @Getter private static Map<String, Long> lastBardPositiveEffectUsage = new HashMap<>();
    @Getter private static Map<String, Long> lastBardNegativeEffectUsage = new HashMap<>();

    @Getter List<PvPClass> pvpClasses = new ArrayList<PvPClass>();

    public PvPClassHandler() {
        pvpClasses.add(new ArcherClass());
        //pvpClasses.add(new RogueClass());
        pvpClasses.add(new OffensiveBardClass());
        pvpClasses.add(new DefensiveBardClass());

        for (PvPClass pvpClass : pvpClasses) {
            FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(pvpClass, FoxtrotPlugin.getInstance());
        }

        FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskTimer(FoxtrotPlugin.getInstance(), this, 2L, 2L);
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
    }

    @Override
    public void run() {
        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            // Cancel kit warmup if the player took off armor
            if (warmupTasks.containsKey(player.getName())) {
                PvPClass trying = warmupTasks.get(player.getName()).getPvpClass();

                if (!trying.qualifies(player.getInventory())) {
                    warmupTasks.get(player.getName()).cancel();
                    warmupTasks.remove(player.getName());
                }
            }

            // Remove kit if player took off armor, otherwise .tick();
            if (equippedKits.containsKey(player.getName())) {
                PvPClass equippedPvPClass = equippedKits.get(player.getName());

                if (!equippedPvPClass.qualifies(player.getInventory())) {
                    equippedKits.remove(player.getName());
                    player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + equippedPvPClass.getName() + ChatColor.GRAY+ " --> " + ChatColor.RED + "Disabled!");
                    equippedPvPClass.remove(player);
                    equippedPvPClass.removeInfiniteEffects(player);
                } else {
                    equippedPvPClass.tick(player);
                }
            }

            // Start kit warmup
            for (PvPClass pvPClass : pvpClasses) {
                if (pvPClass.qualifies(player.getInventory())) {
                    // If they're already warming up
                    if (warmupTasks.containsKey(player.getName()) && warmupTasks.get(player.getName()).getPvpClass() == pvPClass) {
                        continue;
                    }

                    // If they have the kit equipped
                    if (equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvPClass) {
                        continue;
                    }

                    startWarmup(player, pvPClass);
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand() == null) {
            return;
        }

        for (PvPClass pvPClass : pvpClasses) {
            if (hasKitOn(event.getPlayer(), pvPClass) && pvPClass.getConsumables() != null && pvPClass.getConsumables().contains(event.getPlayer().getItemInHand().getType())) {
                if (pvPClass.itemConsumed(event.getPlayer(), event.getItem().getType())) {
                    if (event.getPlayer().getItemInHand().getAmount() > 1) {
                        event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                    } else {
                        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    public static boolean hasKitOn(Player player, PvPClass pvPClass) {
        return (equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvPClass);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (equippedKits.containsKey(event.getPlayer().getName())) {
            equippedKits.get(event.getPlayer().getName()).remove(event.getPlayer());
            equippedKits.remove(event.getPlayer().getName());
        }

        for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1_000_000) {
                event.getPlayer().removePotionEffect(potionEffect.getType());
            }
        }
    }

    public void startWarmup(Player player, PvPClass pvpClass) {
        player.sendMessage("§aClass: §b" + pvpClass.getName() + "§a Equipped. Warm-up: §e" + pvpClass.getWarmup() + "s");

       PvPClassHandler.getWarmupTasks().put(player.getName(), new KitTask(player, pvpClass));
        PvPClassHandler.getWarmupTasks().get(player.getName()).runTaskTimer(FoxtrotPlugin.getInstance(), 20, 20);
    }

    public static class KitTask extends BukkitRunnable {

        Player player;
        @Getter PvPClass pvpClass;
        @Getter long time;

        public KitTask(Player player, PvPClass pvpClass) {
            this.player = player;
            this.pvpClass = pvpClass;
            this.time = System.currentTimeMillis() + (pvpClass.getWarmup() * 1000L);
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                cancel();
                PvPClassHandler.getWarmupTasks().remove(player.getName());
            }

            if (System.currentTimeMillis() >= time) {
                pvpClass.apply(player);

                PvPClassHandler.getEquippedKits().put(player.getName(), pvpClass);
                PvPClassHandler.getWarmupTasks().remove(player.getName());

                player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + pvpClass.getName() + ChatColor.GRAY+ " --> " + ChatColor.GREEN + "Enabled!");
                player.sendMessage(ChatColor.AQUA + "Class Info: " + ChatColor.GREEN + pvpClass.getSiteLink());
                cancel();
            }
        }

    }

}