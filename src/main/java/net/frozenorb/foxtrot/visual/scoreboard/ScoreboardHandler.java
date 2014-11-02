package net.frozenorb.foxtrot.visual.scoreboard;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by chasechocolate.
 */
public class ScoreboardHandler {
    private HashMap<String, FoxtrotBoard> boards = new HashMap<>();

    public ScoreboardHandler(){
        new BukkitRunnable(){
            @Override
            public void run(){
                for(Player online : Bukkit.getOnlinePlayers()){
                    update(online);
                }
            }
        }.runTaskTimer(FoxtrotPlugin.getInstance(), 2L, 2L);
    }

    public void update(Player player){
        if(boards.containsKey(player.getName())){
            boards.get(player.getName()).update();
        } else {
            boards.put(player.getName(), new FoxtrotBoard(player));
        }
    }

    public void remove(Player player){
        boards.remove(player.getName());
    }
}