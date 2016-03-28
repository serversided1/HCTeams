package net.frozenorb.foxtrot.chunklimiter;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 28/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public abstract class PlayerRenderLimiter {

    private int task = -1;
    @Getter
    private int target;
    private UUID playerid;

    public PlayerRenderLimiter( UUID playerid, int targetRender ) {
        this.playerid = playerid;
        target = targetRender;
        task = Bukkit.getScheduler().runTaskTimer( Foxtrot.getInstance(), new Runnable() {
            @Override
            public void run() {
                if( !execute() ) {
                    Foxtrot.getInstance().getServer().getScheduler().cancelTask( task );
                    task = -1;
                    onCompleted();
                }
            }
        },60L,60L ).getTaskId();
    }

    public abstract void onCompleted();

    public void setTarget( int newTarget ) {
        target = newTarget;
    }

    public boolean execute() {
        Player player = Bukkit.getServer().getPlayer( playerid );
        if( player != null ) {
            CraftPlayer cplayer = (CraftPlayer)player;
            int view = cplayer.getViewDistance();//SPIGOT BUILD NOT UP TO DATE SO THIS WILL REDLINE LADS
            if( view > target ) {
                cplayer.setViewDistance( view - 1 );
            } else if( view < target ) {
                cplayer.setViewDistance( view + 1 );
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
