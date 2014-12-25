package net.frozenorb.foxtrot.raffle.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.raffle.enums.RaffleAchievement;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RaffleListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {

            public void run() {
                FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievement(event.getPlayer(), RaffleAchievement.WELCOME_TO_HCT);
            }

        }.runTaskLater(FoxtrotPlugin.getInstance(), 20 * 5);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.LOG) {
            FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievement(event.getPlayer(), RaffleAchievement.LUMBERJACK);
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem() == null || event.getItem().getDurability() != (short) 1 || event.getItem().getType() != Material.GOLDEN_APPLE) {
            return;
        }

        FoxtrotPlugin.getInstance().getRaffleHandler().giveRaffleAchievement(event.getPlayer(), RaffleAchievement.SUPERMAN);
    }

}