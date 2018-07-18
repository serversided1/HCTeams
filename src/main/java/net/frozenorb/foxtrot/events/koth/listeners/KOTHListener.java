package net.frozenorb.foxtrot.events.koth.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.events.EventType;
import net.frozenorb.foxtrot.events.koth.KOTH;
import net.frozenorb.foxtrot.events.koth.events.EventControlTickEvent;
import net.frozenorb.qlib.util.TimeUtils;

public class KOTHListener implements Listener {

    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {
        
        if (event.getKOTH().getType() != EventType.KOTH) {
            return;
        }

        KOTH koth = (KOTH) event.getKOTH();
        if (koth.getRemainingCapTime() % 180 == 0 && koth.getRemainingCapTime() <= (koth.getCapTime() - 30)) {
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + koth.getName() + ChatColor.GOLD + " is trying to be controlled.");
            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.GOLD + " - Time left: " + ChatColor.BLUE + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()));
        }
    }

}