package net.frozenorb.foxtrot.minerworld.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.EndListener;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class LeavingListener implements Listener {

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getFrom().getWorld().equals(Foxtrot.getInstance().getMinerWorldHandler().getWorld())) {
            Location exit = EndListener.getEndReturn();

            exit.subtract(0, 0, exit.getBlockZ() * 2);
            exit.setYaw(exit.getYaw() - 180);

            event.setTo(exit);
        }
    }

}
