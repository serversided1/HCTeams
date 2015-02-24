package net.frozenorb.foxtrot.border;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BorderThread extends Thread {

    public BorderThread() {
        super("Foxtrot - Border Thread");
    }

    public void run() {
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
            Border border = new Border();

            border.scanClaims(player);

            if (border.getClaims().size() == 0) {
                Border.clearPlayer(player);
            } else {
                border.sendToPlayer(player);
            }
        } catch (Exception e) {
            if (player != null && player.isOp()) {
                player.sendMessage(ChatColor.RED + "An exception was thrown while trying to calculate your spawn border");
            }

            e.printStackTrace();
        }
    }

}