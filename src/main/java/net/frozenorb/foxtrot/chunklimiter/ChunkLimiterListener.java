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

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 28/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class ChunkLimiterListener implements Listener {

    public static HashMap<UUID, PlayerRenderLimiter> chunkLimited = new HashMap<>(  );
    private HashMap<UUID, Integer> defaultView = new HashMap<>(  );

    @EventHandler
    public void onPlayerJoin( PlayerJoinEvent event ) {
        defaultView.put( event.getPlayer().getUniqueId(), (( CraftPlayer) event.getPlayer()).getViewDistance() );
    }

    @EventHandler
    public void onPlayerLeave( PlayerQuitEvent event ) {
        defaultView.remove( event.getPlayer().getUniqueId() );
    }

    @EventHandler
    public void onMove( PlayerMoveEvent event ) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        int view = (( CraftPlayer) event.getPlayer()).getViewDistance();
        int target = view;

        Team team = LandBoard.getInstance().getTeam( event.getTo() );
        if( team != null ) {
            if( team.getName().equalsIgnoreCase( "Spawn" ) ) {
                target = 3;
            } else {
                target = defaultView.get( event.getPlayer().getUniqueId() );
            }
        } else {
            if( event.getTo().getBlockY() < 64 && event.getTo().getBlockY() < event.getTo().getWorld().getHighestBlockYAt( event.getTo() ) ) {
                target = 1;
            } else {
                target = defaultView.get( event.getPlayer().getUniqueId() );
            }
        }

        if( target != view ) {
            if ( chunkLimited.containsKey( event.getPlayer().getUniqueId() ) ) {
                PlayerRenderLimiter limiter = chunkLimited.get( event.getPlayer().getUniqueId() );
                if ( limiter.getTarget() != target ) {
                    limiter.setTarget( target );
                }
            } else {
                chunkLimited.put( event.getPlayer().getUniqueId(), new PlayerRenderLimiter( event.getPlayer().getUniqueId(), target ) {
                    @Override
                    public void onCompleted() {
                        chunkLimited.remove( event.getPlayer().getUniqueId() );
                    }
                } );
            }
        }
    }

}
