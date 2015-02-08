package net.frozenorb.foxtrot.server;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.player.PlayerMoveEvent;

public interface RegionMoveHandler {

    public static final RegionMoveHandler ALWAYS_TRUE = new RegionMoveHandler() {

        @Override
        public boolean handleMove(PlayerMoveEvent event) {
            return (true);
        }

    };

    public static final RegionMoveHandler PVP_TIMER = new RegionMoveHandler() {

        @Override
        public boolean handleMove(PlayerMoveEvent event) {
            if (FoxtrotPlugin.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getName()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                event.getPlayer().sendMessage(ChatColor.RED + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                event.setTo(event.getFrom());
                return (false);
            }

            return (true);
        }

    };

    public boolean handleMove(PlayerMoveEvent event);

}