package net.frozenorb.foxtrot.scoreboard;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.spigotmc.CustomTimingsHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chasechocolate.
 */
public class FoxtrotBoard {

    private static CustomTimingsHandler creation = new CustomTimingsHandler("Foxtrot - FB Creation");
    private static CustomTimingsHandler valueGrab = new CustomTimingsHandler("Foxtrot - FB Value Grab");

    @Getter private Player player;
    @Getter private Objective objective;
    @Getter private Set<String> displayedScores = new HashSet<String>();

    public FoxtrotBoard(Player player) {
        creation.startTiming();
        this.player = player;

        Scoreboard board = FoxtrotPlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();

        objective = board.registerNewObjective("HCTeams", "dummy");
        objective.setDisplayName(FoxtrotPlugin.getInstance().getMapHandler().getScoreboardTitle());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        creation.stopTiming();
        update();
        player.setScoreboard(board);
    }

    public void update() {
        for (ScoreGetter getter : ScoreGetter.SCORES) {
            valueGrab.startTiming();
            int seconds = getter.getSeconds(player);
            String title = getter.getTitle(player);
            valueGrab.stopTiming();

            if (seconds == ScoreGetter.NO_SCORE) {
                if (displayedScores.contains(title)) {
                    objective.getScoreboard().resetScores(title);
                    displayedScores.remove(title);
                }
            } else {
                displayedScores.add(title);

                Score score = objective.getScore(title);

                if (score.getScore() != seconds) {
                    score.setScore(seconds);
                }
            }
        }
    }

}