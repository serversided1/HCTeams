package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
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
            List<UUID> uuidList = Foxtrot.getInstance().getIpMap().getValue(ip);
            if(Foxtrot.getInstance().getWhitelistedIPMap().contains(event.getUniqueId())) {
                UUID other = Foxtrot.getInstance().getWhitelistedIPMap().getValue(event.getUniqueId());
                if( !uuidList.contains(other) ) {
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage("Error while logging into HCTeams, please join our teamspeak at ts.minehq.com and our staff can help you resolve this problem");
                }
            } else if( !uuidList.isEmpty() ){
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("Error while logging into HCTeams, please join our teamspeak at ts.minehq.com and our staff can help you resolve this problem");
            }
        }
    }
}
