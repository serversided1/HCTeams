package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class AntiGlitchListener implements Listener {

    @EventHandler(priority= EventPriority.MONITOR)
    public void onVerticalBlockPlaceGlitch(BlockPlaceEvent event) {
        if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null && event.isCancelled()) {
            event.getPlayer().teleport(event.getPlayer().getLocation());
        }
    }

}