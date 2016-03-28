package net.frozenorb.foxtrot.chunklimiter;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class ChunkLimiterListener implements Listener {

    private HashMap<UUID, Integer> defaultView = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        defaultView.put(event.getPlayer().getUniqueId(), ((CraftPlayer) event.getPlayer()).spigot().getViewDistance());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        defaultView.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockY() >> 4 == event.getTo().getBlockY() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4) {
            return;
        }

        int view = ((CraftPlayer) event.getPlayer()).spigot().getViewDistance();
        int target = view;

        Team team = LandBoard.getInstance().getTeam(event.getTo());
        if (team != null && !team.getName().equalsIgnoreCase("Warzone")) {
            if (team.getName().equalsIgnoreCase("spawn")) {
                target = 3;
            } else {
                target = defaultView.get(event.getPlayer().getUniqueId());
            }
        } else {
            if (event.getTo().getBlockY() < 32) {
                target = 1;
            } else {
                target = defaultView.get(event.getPlayer().getUniqueId());
            }
        }

        System.out.println("[" + event.getPlayer().getName() + "]Target has been set to: " + target + " and view is: " + view);

        if (target != view) {
            ((CraftPlayer) event.getPlayer()).spigot().setViewDistance(target);
        }
    }

}
