package net.frozenorb.foxtrot.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.xpacket.FrozenXPacketHandler;
import net.frozenorb.qlib.xpacket.XPacket;
import net.frozenorb.qqueue.qQueue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@AllArgsConstructor
public class PlayerInfoPacket implements XPacket {

    @Getter private String queue;
    @Getter private UUID player;
    @Getter private int totalLives;
    @Getter private long deathbannedUntil;
    @Getter private String livesLocked;

    // We have to have this for XPacket to do its thing.
    public PlayerInfoPacket() {}

    public static void sendResponse(final UUID player) {
        if (Foxtrot.getInstance().getServer().getPluginManager().getPlugin("qQueue") == null) {
            return;
        }

        new BukkitRunnable() {

            public void run() {
                int totalLives = Foxtrot.getInstance().getSoulboundLivesMap().getLives(player) + Foxtrot.getInstance().getFriendLivesMap().getLives(player);
                long deathbannedUntil = Foxtrot.getInstance().getDeathbanMap().getDeathban(player);
                String livesLocked = null;

                if (Foxtrot.getInstance().getServerHandler().getBetrayer(player) != null) {
                    livesLocked = "Betrayer";
                }

                if (Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
                    livesLocked = "EOTW";
                }

                PlayerInfoPacket packet = new PlayerInfoPacket(qQueue.getInstance().getQueueHandler().getQueueId(), player, totalLives, deathbannedUntil, livesLocked);
                FrozenXPacketHandler.sendToAll(packet);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public void onReceive() {}

}