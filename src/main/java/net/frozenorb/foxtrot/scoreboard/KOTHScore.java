package net.frozenorb.foxtrot.scoreboard;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.qlib.scoreboard.ScoreFunction;
import net.frozenorb.qlib.scoreboard.ScoreboardScore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHScore extends ScoreboardScore {

    private String lastActiveKOTH;

    public String getTitle(Player player) {
        return (lastActiveKOTH);
    }

    public String getValue(Player player) {
        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
            if (koth.isHidden() || !koth.isActive()) {
                continue;
            }

            switch (koth.getName()) {
                case "EOTW":
                    lastActiveKOTH = ChatColor.DARK_RED.toString() + ChatColor.BOLD + "EOTW";
                    break;
                case "Citadel":
                    lastActiveKOTH = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                    break;
                default:
                    lastActiveKOTH = ChatColor.BLUE.toString() + ChatColor.BOLD + koth.getName();
                    break;
            }

            return (ScoreFunction.TIME_SIMPLE.apply((float) koth.getRemainingCapTime()));
        }

        return (null);
    }

}