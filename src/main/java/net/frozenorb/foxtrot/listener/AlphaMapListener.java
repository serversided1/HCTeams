package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class AlphaMapListener implements Listener {

    private void startUpdate(final Furnace tile, final int increase) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (tile.getCookTime() > 0 || tile.getBurnTime() > 0) {
                    tile.setCookTime((short) (tile.getCookTime() + increase));
                    tile.update();
                } else {
                    cancel();
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 1L, 1L);
    }

    // ALPHA
    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event){
        Furnace tile = (Furnace) event.getBlock().getState();
        // Averages to 1.5
        startUpdate(tile, FoxtrotPlugin.RANDOM.nextBoolean() ? 1 : 2);
    }

}