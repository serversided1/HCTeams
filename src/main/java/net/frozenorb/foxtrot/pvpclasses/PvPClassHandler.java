package net.frozenorb.foxtrot.pvpclasses;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.MinerClass;
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

    @Getter private static Map<String, PvPClass> equippedKits = new HashMap<String, PvPClass>();
    @Getter List<PvPClass> pvpClasses = new ArrayList<PvPClass>();

    public PvPClassHandler() {
        pvpClasses.add(new ArcherClass());
        pvpClasses.add(new BardClass());
        pvpClasses.add(new MinerClass());

        for (PvPClass pvpClass : pvpClasses) {
            FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(pvpClass, FoxtrotPlugin.getInstance());
        }

        FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskTimer(FoxtrotPlugin.getInstance(), this, 2L, 2L);
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(this, FoxtrotPlugin.getInstance());
    }

    @Override
    public void run() {
        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            // Remove kit if player took off armor, otherwise .tick();
            if (equippedKits.containsKey(player.getName())) {
                PvPClass equippedPvPClass = equippedKits.get(player.getName());

                if (!equippedPvPClass.qualifies(player.getInventory())) {
                    equippedKits.remove(player.getName());
                    player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + equippedPvPClass.getName() + ChatColor.GRAY+ " --> " + ChatColor.RED + "Disabled!");
                    equippedPvPClass.remove(player);
                    PvPClass.removeInfiniteEffects(player);
                } else {
                    equippedPvPClass.tick(player);
                }
            } else {
                // Start kit warmup
                for (PvPClass pvpClass : pvpClasses) {
                    if (pvpClass.qualifies(player.getInventory())) {
                        if (pvpClass.canApply(player)) {
                            pvpClass.apply(player);
                            PvPClassHandler.getEquippedKits().put(player.getName(), pvpClass);

                            player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + pvpClass.getName() + ChatColor.GRAY+ " --> " + ChatColor.GREEN + "Enabled!");
                            player.sendMessage(ChatColor.AQUA + "Class Info: " + ChatColor.GREEN + pvpClass.getSiteLink());
                        }
                    }
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

}