package net.frozenorb.foxtrot.pvpclasses;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.MinerClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.RogueClass;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SuppressWarnings("deprecation")
public class PvPClassHandler extends BukkitRunnable implements Listener {

    @Getter private static Map<String, PvPClass> equippedKits = new HashMap<>();
    @Getter private static Map<UUID, PvPClass.SavedPotion> savedPotions = new HashMap<>();
    @Getter List<PvPClass> pvpClasses = new ArrayList<>();

    public PvPClassHandler() {
        if (!Foxtrot.getInstance().getServerHandler().isSquads()) {
            pvpClasses.add(new ArcherClass());
            pvpClasses.add(new BardClass());
            pvpClasses.add(new RogueClass());
        }
        pvpClasses.add(new MinerClass());

        for (PvPClass pvpClass : pvpClasses) {
            Foxtrot.getInstance().getServer().getPluginManager().registerEvents(pvpClass, Foxtrot.getInstance());
        }

        Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), this, 2L, 2L);
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    @Override
    public void run() {
        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            // Remove kit if player took off armor, otherwise .tick();
            if (equippedKits.containsKey(player.getName())) {
                PvPClass equippedPvPClass = equippedKits.get(player.getName());

                if (!equippedPvPClass.qualifies(player.getInventory())) {
                    equippedKits.remove(player.getName());
                    player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + equippedPvPClass.getName() + ChatColor.GRAY+ " --> " + ChatColor.RED + "Disabled!");
                    equippedPvPClass.remove(player);
                    PvPClass.removeInfiniteEffects(player);
                } else if(!player.hasMetadata("frozen")){
                    equippedPvPClass.tick(player);
                }
            } else {
                // Start kit warmup
                for (PvPClass pvpClass : pvpClasses) {
                    if (pvpClass.qualifies(player.getInventory()) && pvpClass.canApply(player) && !player.hasMetadata("frozen")) {
                        pvpClass.apply(player);
                        PvPClassHandler.getEquippedKits().put(player.getName(), pvpClass);

                        player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + pvpClass.getName() + ChatColor.GRAY+ " --> " + ChatColor.GREEN + "Enabled!");
                        player.sendMessage(ChatColor.AQUA + "Class Info: " + ChatColor.GREEN + pvpClass.getSiteLink());
                    }
                }
            }
        }
        checkSavedPotions();
    }

    public void checkSavedPotions() {
        Iterator<Map.Entry<UUID, PvPClass.SavedPotion>> idIterator = savedPotions.entrySet().iterator();
        while( idIterator.hasNext() ) {
            Map.Entry<UUID, PvPClass.SavedPotion> id = idIterator.next();
            Player player = Bukkit.getPlayer(id.getKey());
            if( player != null && player.isOnline() ) {
                if( id.getValue().getTime() < System.currentTimeMillis() ) {
                    if(player.addPotionEffect(id.getValue().getPotionEffect())) {
                        idIterator.remove();
                    }
                }
            } else {
                idIterator.remove();
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
                        event.getPlayer().getInventory().remove(event.getPlayer().getItemInHand());
                        //event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    }
                }
            }
        }
    }

    public static PvPClass getPvPClass(Player player) {
        return (equippedKits.containsKey(player.getName()) ? equippedKits.get(player.getName()) : null);
    }

    public static boolean hasKitOn(Player player, PvPClass pvpClass) {
        return (equippedKits.containsKey(player.getName()) && equippedKits.get(player.getName()) == pvpClass);
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