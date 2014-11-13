package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Created by macguy8 on 11/12/2014.
 */
public class AntiGlitchListener implements Listener {

    @EventHandler(priority= EventPriority.MONITOR)
    public void onVerticalBlockPlaceGlitch(BlockPlaceEvent event) {
        if (FoxtrotPlugin.getInstance().getTeamHandler().isTaken(event.getBlock().getLocation()) && event.isCancelled()) {
            event.getPlayer().teleport(event.getPlayer().getLocation());
        }
    }

}