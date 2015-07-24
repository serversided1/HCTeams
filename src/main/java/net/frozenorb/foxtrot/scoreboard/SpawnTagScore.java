package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnTagScore extends ScoreboardScore {

    public SpawnTagScore() {
        super(ChatColor.RED.toString() + ChatColor.BOLD + "Spawn Tag");
    }

    @Override
    public String getValue(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

}