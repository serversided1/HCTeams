package net.frozenorb.foxtrot.border;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Date;

public class BorderThread extends Thread {

    @Getter private static String stateString = "Not started";
    @Getter private static long stateUpdated = System.currentTimeMillis();

    public BorderThread() {
        super("Foxtrot - Border Thread");
    }

    public void run() {
        try {
            while (true) {
                setState("Looping players");

                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    setState("Checking " + player.getName());
                    checkPlayer(player);
                }

                setState("Finished looping players");

                try {
                    setState("Sleeping...");
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    setState("Interrupted while sleeping");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            setState("Uncaught exception: " + e.getMessage() + " @ " + new Date());
            e.printStackTrace();
        }
}

    public void setState(String state) {
        BorderThread.stateString = state;
        BorderThread.stateUpdated = System.currentTimeMillis();
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