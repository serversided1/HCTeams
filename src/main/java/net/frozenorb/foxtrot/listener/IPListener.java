package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.UUID;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class IPListener implements Listener {

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        String ip = event.getAddress().getHostName();
        if(Foxtrot.getInstance().getIpMap().contains(ip)) {
            List<UUID> uuidList = Foxtrot.getInstance().getIpMap().get(ip);
            if(Foxtrot.getInstance().getWhitelistedIPMap().contains(event.getUniqueId())) {
                UUID other = Foxtrot.getInstance().getWhitelistedIPMap().get(event.getUniqueId());
                if( !uuidList.contains(other) && !uuidList.contains(event.getUniqueId()) ) {
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage("We have detected you as a potential alternate account of " + Bukkit.getOfflinePlayer(other) + ". Please join our teamspeak (ts.minehq.com) and speak with our staff to resolve this issue.");
                }
            } else if( !uuidList.isEmpty() && !uuidList.contains(event.getUniqueId()) ){
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("We have detected you as a potential alternate account of " + Bukkit.getOfflinePlayer(uuidList.get(0)) + ". Please join our teamspeak (ts.minehq.com) and speak with our staff to resolve this issue.");
            }
        }
        if( !event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.KICK_OTHER) ) {
            Foxtrot.getInstance().getIpMap().add(ip, event.getUniqueId());
        }
    }
}
