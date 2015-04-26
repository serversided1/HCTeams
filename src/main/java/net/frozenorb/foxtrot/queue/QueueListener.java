package net.frozenorb.foxtrot.queue;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class QueueListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (Foxtrot.getInstance().getQueueHandler().getMoving().remove(event.getPlayer().getName())) {
            Foxtrot.getInstance().getLogger().info(event.getPlayer().getName() + " joined when requested to join by the queue.");

            if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL) {
                event.setResult(PlayerLoginEvent.Result.ALLOWED);
            }
        }
    }

}