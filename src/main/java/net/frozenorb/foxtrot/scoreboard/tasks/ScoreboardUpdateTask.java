package net.frozenorb.foxtrot.scoreboard.tasks;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.scoreboard.ScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdateTask extends BukkitRunnable {

    public void run() {
        if (ScoreboardHandler.scoreboardTimerEnabled) {
            for (Player online : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                FoxtrotPlugin.getInstance().getScoreboardHandler().update(online);
            }
        }
    }

}