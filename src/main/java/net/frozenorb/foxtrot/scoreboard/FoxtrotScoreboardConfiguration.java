package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.scoreboard.ScoreboardConfiguration;
import net.frozenorb.qlib.scoreboard.TitleGetter;

public class FoxtrotScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        ScoreboardConfiguration configuration = new ScoreboardConfiguration();

        configuration.setTitleGetter(new TitleGetter(Foxtrot.getInstance().getMapHandler().getScoreboardTitle()));
        configuration.setScoreGetter(new FoxtrotScoreGetter());

        return (configuration);
    }

}