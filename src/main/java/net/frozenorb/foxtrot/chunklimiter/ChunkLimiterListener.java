package net.frozenorb.foxtrot.chunklimiter;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChunkLimiterListener implements Listener {

    private HashMap<UUID, Integer> defaultView = new HashMap<>();
    @Getter private static HashMap<UUID, Integer> viewDistances = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //defaultView.put(event.getPlayer().getUniqueId(), ((CraftPlayer) event.getPlayer()).spigot().getViewDistance());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        defaultView.remove(event.getPlayer().getUniqueId());
        viewDistances.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockY() >> 4 == event.getTo().getBlockY() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4) {
            return;
        }

        if( viewDistances.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        /*int view = ((CraftPlayer) event.getPlayer()).spigot().getViewDistance();
        int target = view;

        Team team = LandBoard.getInstance().getTeam(event.getTo());
        if (team != null && !team.getKitName().equalsIgnoreCase("Warzone")) {
            if (team.getKitName().equalsIgnoreCase("spawn")) {
                target = 3;
            } else {
                target = defaultView.get(event.getPlayer().getUniqueId());
            }
        } else {
            if (event.getTo().getBlockY() < 20) {
                target = 1;
            } else {
                target = defaultView.get(event.getPlayer().getUniqueId());
            }
        }

        if (target != view) {
            ((CraftPlayer) event.getPlayer()).spigot().setViewDistance(target);
        }
        */
    }

}
