package net.frozenorb.foxtrot.minerworld.listener;

import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SafeZoneListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() != GameMode.CREATIVE && !player.hasMetadata("modmode")) {
            if (DTRBitmask.DENY_REENTRY.appliesAt(event.getTo()) && !DTRBitmask.DENY_REENTRY.appliesAt(event.getFrom())) {
                event.setCancelled(true);
            }
        }
    }

}
