package net.frozenorb.foxtrot.packetborder;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.entity.Player;

public class PacketBorderThread extends Thread {

    public PacketBorderThread() {
        super("Foxtrot - Packet Border Thread");
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                try {
                    checkPlayer(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkPlayer(Player player) {
        try {
            PacketBorder packetBorder = new PacketBorder();

            packetBorder.scanClaims(player);

            if (packetBorder.getClaims().size() == 0) {
                PacketBorder.clearPlayer(player);
            } else {
                packetBorder.sendToPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}