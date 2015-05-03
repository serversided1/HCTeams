package net.frozenorb.foxtrot.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.xpacket.FrozenXPacketHandler;
import net.frozenorb.qlib.xpacket.XPacket;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@AllArgsConstructor
public class PlayerInfoPacket implements XPacket {

    @Getter private UUID player;
    @Getter private int totalLives;
    @Getter private long deathbannedUntil;

    // We have to have this for XPacket to do its thing.
    public PlayerInfoPacket() {}

    public static void sendResponse(final UUID player) {
        new BukkitRunnable() {

            public void run() {
                int totalLives = Foxtrot.getInstance().getSoulboundLivesMap().getLives(player) + Foxtrot.getInstance().getFriendLivesMap().getLives(player) + Foxtrot.getInstance().getTransferableLivesMap().getLives(player);
                long deathbannedUntil = Foxtrot.getInstance().getDeathbanMap().getDeathban(player);

                PlayerInfoPacket packet = new PlayerInfoPacket(player, totalLives, deathbannedUntil);
                FrozenXPacketHandler.sendToAll(packet);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public void onReceive() {}

}