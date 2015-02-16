package net.frozenorb.foxtrot.scoreboard;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chasechocolate.
 */
public class ScoreboardHandler {

    private Map<String, FoxtrotBoard> boards = new ConcurrentHashMap<String, FoxtrotBoard>();
    public static boolean scoreboardTimerEnabled = true;

    public void create(Player player) {
        boards.put(player.getName(), new FoxtrotBoard(player));
    }

    public void update(Player player) {
        if (boards.containsKey(player.getName())) {
            boards.get(player.getName()).update();
        }
    }

    public void remove(Player player){
        boards.remove(player.getName());
    }

}