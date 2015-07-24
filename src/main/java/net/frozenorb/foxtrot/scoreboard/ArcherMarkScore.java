package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArcherMarkScore extends ScoreboardScore {

    public ArcherMarkScore() {
        super(ChatColor.GOLD.toString() + ChatColor.BOLD + "Archer Mark");
    }

    public String getValue(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

}