package net.frozenorb.foxtrot.queue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.qlib.xpacket.XPacket;
import net.frozenorb.qqueue.qQueue;

import java.util.UUID;

@AllArgsConstructor
public class RequestPlayerInfoPacket implements XPacket {

    @Getter private String queue;
    @Getter private UUID player;

    // We have to have this for XPacket to do its thing.
    public RequestPlayerInfoPacket() {}

    public void onReceive() {
        if (!qQueue.getInstance().getQueueHandler().getQueueId().equals(queue)) {
            return;
        }

        PlayerInfoPacket.sendResponse(player);
    }

}