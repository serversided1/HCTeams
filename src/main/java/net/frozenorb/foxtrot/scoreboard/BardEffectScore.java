package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BardEffectScore extends ScoreboardScore {

    public BardEffectScore() {
        super(ChatColor.GREEN.toString() + ChatColor.BOLD + "Bard Effect");
    }

    public String getValue(Player player) {
        if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }

        return (null);
    }

}