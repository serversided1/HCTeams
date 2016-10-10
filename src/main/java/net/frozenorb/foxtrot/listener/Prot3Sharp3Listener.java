package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Prot3Sharp3Listener implements Listener {

    public Prot3Sharp3Listener() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isSquads()) {
                    return;
                }

                for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                    if (!Foxtrot.getInstance().getP3S3AckMap().acknowledgedP3S3(player.getUniqueId())) {
                        player.sendMessage("");
                        player.sendMessage("");
                        player.sendMessage("");
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "This map's kit has changed to");
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Protection 3 Sharpness 3");
                        player.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Acknowledge this message with /prot3sharp3");
                        player.sendMessage("");
                        player.sendMessage("");
                        player.sendMessage("");
                    }
                }
            }

        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap() || Foxtrot.getInstance().getServerHandler().isSquads()) {
            return;
        }
        
        if (!event.getPlayer().hasPlayedBefore()) {
            // new players not bothered by this
            Foxtrot.getInstance().getP3S3AckMap().acknowledgedP3S3(event.getPlayer().getUniqueId());
        }
    }

}