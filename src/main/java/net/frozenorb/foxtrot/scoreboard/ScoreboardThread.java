package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.entity.Player;

public class ScoreboardThread extends Thread {

    public ScoreboardThread() {
        super("Foxtrot - Scoreboard Thread");
    }

    public void run() {
        try {
            while (true) {
                if (ScoreboardHandler.scoreboardTimerEnabled) {
                    for (Player online : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                        FoxtrotPlugin.getInstance().getScoreboardHandler().update(online);
                    }
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}