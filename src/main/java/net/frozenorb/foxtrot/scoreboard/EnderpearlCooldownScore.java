package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.listener.EnderpearlListener;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EnderpearlCooldownScore extends ScoreboardScore {

    public EnderpearlCooldownScore() {
        super(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Enderpearl");
    }

    public String getValue(Player player) {
        if (EnderpearlListener.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlListener.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = EnderpearlListener.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

            if (diff >= 0) {
                return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
            }
        }

        return (null);
    }

}