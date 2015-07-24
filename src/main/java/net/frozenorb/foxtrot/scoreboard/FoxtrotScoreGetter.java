package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.qlib.scoreboard.ScoreGetter;
import net.frozenorb.qlib.scoreboard.ScoreboardScore;
import org.bukkit.entity.Player;

// Foxtrot has a bunch of constant scores, so this is really simple.
public class FoxtrotScoreGetter implements ScoreGetter {

    private static ScoreboardScore[] SCORES = new ScoreboardScore[] {

            new SpawnTagScore(),
            new EnderpearlCooldownScore(),
            new PvPTimerScore(),
            new KOTHScore(),
            new ArcherMarkScore(),
            new BardBuffScore(),
            new BardEnergyScore()

    };

    @Override
    public ScoreboardScore[] getScores(Player player) {
        return (SCORES);
    }

}