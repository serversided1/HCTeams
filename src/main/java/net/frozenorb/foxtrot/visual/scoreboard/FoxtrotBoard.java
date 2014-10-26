package net.frozenorb.foxtrot.visual.scoreboard;

import lombok.Getter;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.foxtrot.util.WrappedPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Created by chasechocolate.
 */
public class FoxtrotBoard {
    public static final String OBJECTIVE_NAME = "smoke_weed";
    public static final String SCOREBOARD_TITLE = ChatColor.GOLD + "" + ChatColor.BOLD + "HCTeams" + ChatColor.RED + " [Alpha]";

    @Getter
    private Player player;

    @Getter
    private Objective obj;

    public FoxtrotBoard(Player player){
        this.player = player;

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        obj = board.registerNewObjective(OBJECTIVE_NAME, "dummy");
        obj.setDisplayName(SCOREBOARD_TITLE);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        update();
        player.setScoreboard(board);
    }

    public void update(){
        if(!(player.isOnline())){
            return;
        }

        int scores = 0;
        int nextVal = 14;

        for(ScoreGetter getter : ScoreGetter.SCORES){
            String title = getter.getTitle(player);
            long millis = getter.getMillis(player);

            if(millis == ScoreGetter.NO_SCORE){
                removeTeam(title);
                obj.getScoreboard().resetScores(title);
            } else {
                Score score = obj.getScore(title);

                score.setScore(nextVal);
                getTeam(title, millis).addEntry(title);
                scores += 1;
                nextVal -= 1;
            }
        }

        if(scores > 0){
            obj.getScore(ChatColor.RESET + " ").setScore(15);
        } else {
            obj.getScoreboard().resetScores(ChatColor.RESET + " ");
        }
    }

    private Team getTeam(String title, long millis){
        String name = ChatColor.stripColor(title);
        Team team = obj.getScoreboard().getTeam(name);

        if(team == null){
            team = obj.getScoreboard().registerNewTeam(name);
        }


        String time;
        double secs = (millis / 1000.0D);

        if(secs >= 60){
            time = TimeUtils.getMMSS((int) secs);
        } else {
            time = Math.round(10.0D * secs) / 10.0D + "s";
        }

        //team.setPrefix("» "); //Do we want this?
        team.setSuffix(ChatColor.GRAY + ": " + ChatColor.RED + time);
        return team;
    }

    private void removeTeam(String title){
        String name = ChatColor.stripColor(title);
        Team team = obj.getScoreboard().getTeam(name);

        if(team != null){
            team.unregister();
        }
    }
}