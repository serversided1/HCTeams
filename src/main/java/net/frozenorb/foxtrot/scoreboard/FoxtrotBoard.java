package net.frozenorb.foxtrot.scoreboard;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.spigotmc.CustomTimingsHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chasechocolate.
 */
public class FoxtrotBoard {

    private CustomTimingsHandler creation = new CustomTimingsHandler("Foxtrot - FB Creation");
    private CustomTimingsHandler valueGrab = new CustomTimingsHandler("Foxtrot - FB Value Grab");
    private CustomTimingsHandler teamUpdate = new CustomTimingsHandler("Foxtrot - FB Team Update");

    @Getter private Player player;
    @Getter private Objective obj;
    @Getter private Set<String> displayedScores = new HashSet<String>();

    public FoxtrotBoard(Player player) {
        creation.startTiming();
        this.player = player;

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        obj = board.registerNewObjective("HCTeams", "dummy");
        obj.setDisplayName(FoxtrotPlugin.getInstance().getMapHandler().getScoreboardTitle());
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        creation.stopTiming();
        update();
        player.setScoreboard(board);
    }

    public void update() {
        int nextVal = 14;

        for (ScoreGetter getter : ScoreGetter.SCORES) {
            valueGrab.startTiming();
            int seconds = getter.getSeconds(player);
            String title = getter.getTitle(player);
            valueGrab.stopTiming();

            if (seconds == ScoreGetter.NO_SCORE) {
                if (displayedScores.contains(title)) {
                    obj.getScoreboard().resetScores(title);
                    displayedScores.remove(title);
                }
            } else {
                displayedScores.add(title);
                obj.getScore(title).setScore(nextVal);
                getTeam(title, seconds, getter.isRaw()).addEntry(title);
                nextVal -= 1;
            }
        }

        if (nextVal < 14) {
            obj.getScore(ChatColor.RESET + " ").setScore(15);
        } else {
            obj.getScoreboard().resetScores(ChatColor.RESET + " ");
        }
    }

    private Team getTeam(String title, int seconds, boolean raw) {
        teamUpdate.startTiming();
        String name = ChatColor.stripColor(title);
        Team team = obj.getScoreboard().getTeam(name);

        if (team == null) {
            team = obj.getScoreboard().registerNewTeam(name);
        }

        String time = raw ? String.valueOf(seconds) : TimeUtils.getMMSS(seconds);

        team.setSuffix(ChatColor.GRAY + ": " + ChatColor.RED + time);
        teamUpdate.stopTiming();

        return (team);
    }

}