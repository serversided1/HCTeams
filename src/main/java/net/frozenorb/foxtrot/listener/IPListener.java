package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.configuration.Configuration;
import net.frozenorb.qlib.configuration.annotations.ConfigData;
import net.frozenorb.qlib.uuid.FrozenUUIDCache;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ---------- hcteams ----------
 * Created by Fraser.Cumming on 29/03/2016.
 * © 2016 Fraser Cumming All Rights Reserved
 */
public class IPListener extends Configuration implements Listener {

    @ConfigData( path = "IP.IP_WHITELIST" )
    private List<String> list = new ArrayList<>();

    public IPListener() {
        super(Foxtrot.getInstance(), "ip_whitelist.yml", "./");
        list.add("162.248.88.204");
        list.add("31.186.251.7");
        list.add("208.146.44.197");
        load();
        save();
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        String ip = event.getAddress().getHostAddress();
        if( !list.contains(ip) && Foxtrot.getInstance().getIpMap().contains(ip)) {
            List<UUID> uuidList = Foxtrot.getInstance().getIpMap().get(ip);
            if(Foxtrot.getInstance().getWhitelistedIPMap().contains(event.getUniqueId())) {
                UUID other = Foxtrot.getInstance().getWhitelistedIPMap().get(event.getUniqueId());
                if( !uuidList.contains(other) && !uuidList.contains(event.getUniqueId()) ) {
                    event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    event.setKickMessage("We have detected you as a potential alternate account of " + FrozenUUIDCache.name(other) + ". Please join our teamspeak (ts.minehq.com) and speak with our staff to resolve this issue.");
                }
            } else if( !uuidList.isEmpty() && !uuidList.contains(event.getUniqueId()) ){
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage("We have detected you as a potential alternate account of " + FrozenUUIDCache.name(uuidList.get(0)) + ". Please join our teamspeak (ts.minehq.com) and speak with our staff to resolve this issue.");
            }
        }
        if( !event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.KICK_OTHER) ) {
            Foxtrot.getInstance().getIpMap().add(ip, event.getUniqueId());
        }
    }
}
