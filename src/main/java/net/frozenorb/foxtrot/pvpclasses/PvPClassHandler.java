package net.frozenorb.foxtrot.pvpclasses;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.MinerClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.RogueClass;
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

    @Getter List<PvPClass> pvpClasses = new ArrayList<PvPClass>();

    public PvPClassHandler() {
        pvpClasses.add(new ArcherClass());
        pvpClasses.add(new BardClass());
        pvpClasses.add(new MinerClass());
        pvpClasses.add(new RogueClass());

        for (PvPClass pvPClass : pvpClasses) {
            FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(pvPClass, FoxtrotPlugin.getInstance());
        }
    }

	@Override
	public void run() {
		for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            // Cancel kit warmup if the player took off armor
			if (warmupTasks.containsKey(player.getName())) {
				PvPClass trying = warmupTasks.get(player.getName()).getPvPClass();

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
                    equippedPvPClass.remove(player);
				} else {
                    equippedPvPClass.tick(player);
                }
			}

            // Start kit warmup
			for (PvPClass pvPClass : pvpClasses) {
				if (pvPClass.qualifies(player.getInventory())) {
                    // If they're already warming up
					if (warmupTasks.containsKey(player.getName()) && warmupTasks.get(player.getName()).getPvPClass() == pvPClass) {
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

    public void startWarmup(final Player player, PvPClass pvPClass) {
        player.sendMessage("§aClass: §b" + pvPClass.getName() + "§a Enabled. Warm-up: §e" + pvPClass.getWarmup() + "s");

        PvPClassHandler.getWarmupTasks().put(player.getName(), new PvPClassHandler.KitTask(pvPClass, pvPClass.getWarmup()) {

            @Override
            public void run() {
                seconds--;

                if (!player.isOnline()) {
                    cancel();
                    PvPClassHandler.getWarmupTasks().remove(player.getName());
                }

                if (seconds == 0){
                    this.pvPClass.apply(player);
                    PvPClassHandler.getEquippedKits().put(player.getName(), this.pvPClass);
                    PvPClassHandler.getWarmupTasks().remove(player.getName());
                    player.sendMessage(ChatColor.AQUA + "Class: " + ChatColor.BOLD + this.pvPClass.getName() + ChatColor.GRAY+ " --> " + ChatColor.GREEN + "Enabled!");
                    cancel();
                }
            }
        });

        PvPClassHandler.getWarmupTasks().get(player.getName()).runTaskTimer(FoxtrotPlugin.getInstance(), 20, 20);
    }

    public abstract static class KitTask extends BukkitRunnable {

        @Getter
        PvPClass pvPClass;
        @Getter int seconds;
        @Getter long ends;

        public KitTask(PvPClass pvPClass, int seconds){
            this.pvPClass = pvPClass;
            this.seconds = seconds;
            this.ends = System.currentTimeMillis() + (seconds * 1000L);
        }

    }

}