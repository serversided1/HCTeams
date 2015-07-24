package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.qlib.scoreboard.ScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BardEnergyScore extends ScoreboardScore {

    public BardEnergyScore() {
        super(ChatColor.AQUA.toString() + ChatColor.BOLD + "Energy");
    }

    public String getValue(Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            float energy = BardClass.getEnergy().get(player.getName());

            if (energy > 0) {
                // No function here, as it's a "raw" value.
                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
            }
        }

        return (null);
    }

}