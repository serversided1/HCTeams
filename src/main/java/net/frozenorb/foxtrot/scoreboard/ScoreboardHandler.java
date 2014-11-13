package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chasechocolate.
 */
public class ScoreboardHandler {

    private Map<String, FoxtrotBoard> boards = new HashMap<>();
    public static boolean scoreboardTimerEnabled = true;

    public ScoreboardHandler() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (scoreboardTimerEnabled) {
                    for (Player online : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                        update(online);
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 2L, 2L); // Possible lag cause?
    }

    public void update(Player player) {
        if (boards.containsKey(player.getName())) {
            boards.get(player.getName()).update();
        } else {
            boards.put(player.getName(), new FoxtrotBoard(player));
        }
    }

    public void remove(Player player){
        boards.remove(player.getName());
    }

}