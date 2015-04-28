package net.frozenorb.foxtrot.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.xpacket.XPacket;

import java.util.UUID;

@AllArgsConstructor
public class UseLifePacket implements XPacket {

    @Getter private UUID player;

    // We have to have this for XPacket to do its thing.
    public UseLifePacket() {}

    public void onReceive() {
        int soulboundLives = Foxtrot.getInstance().getSoulboundLivesMap().getLives(player);
        int friendLives = Foxtrot.getInstance().getFriendLivesMap().getLives(player);
        int transferableLives = Foxtrot.getInstance().getTransferableLivesMap().getLives(player);

        if (soulboundLives == 0) {
            if (friendLives == 0) {
                // Use a transferable life.
                Foxtrot.getInstance().getTransferableLivesMap().setLives(player, transferableLives - 1);
            } else {
                // Use a friend life.
                Foxtrot.getInstance().getFriendLivesMap().setLives(player, friendLives - 1);
            }
        } else {
            // Use a soulbound life.
            Foxtrot.getInstance().getSoulboundLivesMap().setLives(player, soulboundLives - 1);
        }

        Foxtrot.getInstance().getDeathbanMap().revive(player);
        PlayerInfoPacket.sendResponse(player);
    }

}