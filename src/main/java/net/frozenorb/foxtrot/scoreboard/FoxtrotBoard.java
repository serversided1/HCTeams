package net.frozenorb.foxtrot.scoreboard;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * Created by chasechocolate.
 */
public class FoxtrotBoard {

    @Getter private Player player;
    @Getter private Objective obj;

    public FoxtrotBoard(Player player) {
        this.player = player;

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        obj = board.registerNewObjective("HCTeams", "dummy");
        obj.setDisplayName(FoxtrotPlugin.getInstance().getMapHandler().getScoreboardTitle());
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        update();
        player.setScoreboard(board);
    }

    public void update() {
        int nextVal = 14;

        for (ScoreGetter getter : ScoreGetter.SCORES) {
            long millis = getter.getMillis(player);
            String title = getter.getTitle(player);

            if (millis == ScoreGetter.NO_SCORE) {
                removeTeam(title);
                obj.getScoreboard().resetScores(title);
            } else {
                Score score = obj.getScore(title);

                score.setScore(nextVal);
                getTeam(title, millis).addEntry(title);
                nextVal -= 1;
            }
        }

        if (nextVal < 14) {
            obj.getScore(ChatColor.RESET + " ").setScore(15);
        } else {
            obj.getScoreboard().resetScores(ChatColor.RESET + " ");
        }
    }

    private Team getTeam(String title, long millis) {
        String name = ChatColor.stripColor(title);
        Team team = obj.getScoreboard().getTeam(name);

        if (team == null) {
            team = obj.getScoreboard().registerNewTeam(name);
        }

        String time;
        double secs = (millis / 1000.0D);

        if (secs >= 60) {
            time = TimeUtils.getMMSS((int) secs);
        } else {
            time = Math.round(10.0D * secs) / 10.0D + "s";
        }

        //team.setPrefix("» "); //Do we want this?
        team.setSuffix(ChatColor.GRAY + ": " + ChatColor.RED + time);

        return (team);
    }

    private void removeTeam(String title) {
        Team team = obj.getScoreboard().getTeam(ChatColor.stripColor(title));

        if (team != null) {
            team.unregister();
        }
    }

}