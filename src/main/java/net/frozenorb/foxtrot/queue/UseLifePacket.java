package net.frozenorb.foxtrot.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.xpacket.XPacket;
import net.frozenorb.qqueue.qQueue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@AllArgsConstructor
public class UseLifePacket implements XPacket {

    @Getter private String queue;
    @Getter private UUID player;

    // We have to have this for XPacket to do its thing.
    public UseLifePacket() {}

    public void onReceive() {
        if (Foxtrot.getInstance().getServer().getPluginManager().getPlugin("qQueue") == null || !qQueue.getInstance().getQueueHandler().getQueueId().equalsIgnoreCase(queue)) {
            return;
        }

        new BukkitRunnable() {

            public void run() {
                int soulboundLives = Foxtrot.getInstance().getSoulboundLivesMap().getLives(player);
                int friendLives = Foxtrot.getInstance().getFriendLivesMap().getLives(player);

                if (soulboundLives <= 0) {
                    Foxtrot.getInstance().getFriendLivesMap().setLives(player, friendLives - 1);
                } else {
                    // Use a soulbound life.
                    Foxtrot.getInstance().getSoulboundLivesMap().setLives(player, soulboundLives - 1);
                }

                Foxtrot.getInstance().getDeathbanMap().revive(player);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}
