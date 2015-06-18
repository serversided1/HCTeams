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

    @Getter private UUID player;
    @Getter private int totalLives;
    @Getter private long deathbannedUntil;
    @Getter private boolean betrayer;

    // We have to have this for XPacket to do its thing.
    public PlayerInfoPacket() {}

    public static void sendResponse(final UUID player) {
        if (Foxtrot.getInstance().getServer().getPluginManager().getPlugin("qQueue") == null) {
            return;
        }
        
        new BukkitRunnable() {

            public void run() {
                int totalLives = Foxtrot.getInstance().getSoulboundLivesMap().getLives(player) + Foxtrot.getInstance().getFriendLivesMap().getLives(player) + Foxtrot.getInstance().getTransferableLivesMap().getLives(player);
                long deathbannedUntil = Foxtrot.getInstance().getDeathbanMap().getDeathban(player);
                boolean betrayer = Foxtrot.getInstance().getServerHandler().getBetrayers().contains(player);

                PlayerInfoPacket packet = new PlayerInfoPacket(player, totalLives, deathbannedUntil, betrayer);
                FrozenXPacketHandler.sendToAll(packet);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public void onReceive() {}

}