package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.entity.Player;

public class ScoreboardThread extends Thread {

    public ScoreboardThread() {
        super("Foxtrot - Scoreboard Thread");
    }

    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                for (Player online : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    try {
                        FoxtrotPlugin.getInstance().getScoreboardHandler().update(online);
                    } catch (Exception e) {
                        FoxtrotPlugin.getInstance().getBugSnag().notify(e);
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

}