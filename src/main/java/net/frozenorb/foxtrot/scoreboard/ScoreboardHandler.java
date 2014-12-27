package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.scoreboard.tasks.ScoreboardUpdateTask;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chasechocolate.
 */
public class ScoreboardHandler {

    private Map<String, FoxtrotBoard> boards = new HashMap<>();
    public static boolean scoreboardTimerEnabled = true;

    public ScoreboardHandler() {
        (new ScoreboardUpdateTask()).runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
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