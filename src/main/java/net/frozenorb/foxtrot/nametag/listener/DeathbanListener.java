package net.frozenorb.foxtrot.nametag.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        String hostName = event.getHostname();

        // Do we need the hostname bypass?
        if (hostName.startsWith("bypass1324132") || event.getPlayer().isOp()) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer())) {
            Long unbannedOn = FoxtrotPlugin.getInstance().getDeathbanMap().getValue(event.getPlayer().getName());
            long left = unbannedOn - System.currentTimeMillis();

            event.disallow(org.bukkit.event.player.PlayerLoginEvent.Result.KICK_BANNED, ChatColor.RED + "You are death-banned for another " + TimeUtils.getDurationBreakdown(left) + ".");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        int seconds = FoxtrotPlugin.getInstance().getServerHandler().getDeathBanAt(event.getEntity().getName(), event.getEntity().getLocation());
        FoxtrotPlugin.getInstance().getDeathbanMap().deathban(event.getEntity(), seconds);

        final String time = TimeUtils.getDurationBreakdown(seconds * 1000);

        FoxtrotPlugin.getInstance().getServer().getScheduler().runTaskLater(FoxtrotPlugin.getInstance(), new Runnable() {

            @Override
            public void run() {
                event.getEntity().teleport(event.getEntity().getLocation().add(0, 100, 0));
                event.getEntity().kickPlayer("§c" + event.getDeathMessage() + "\n§cCome back in " + time + "!");

            }
        }, 2L);
    }

}