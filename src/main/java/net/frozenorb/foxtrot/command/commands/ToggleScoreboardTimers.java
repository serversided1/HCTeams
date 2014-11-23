package net.frozenorb.foxtrot.command.commands;

import net.frozenorb.foxtrot.command.annotations.Command;
import net.frozenorb.foxtrot.scoreboard.ScoreboardHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by macguy8 on 11/11/2014.
 */
public class ToggleScoreboardTimers {

    @Command(names={ "ToggleScoreboardTimers" }, permissionNode="op")
    public static void toggleScoreboardTimers(Player sender) {
        ScoreboardHandler.scoreboardTimerEnabled = !ScoreboardHandler.scoreboardTimerEnabled;
        sender.sendMessage(ChatColor.YELLOW + "Scoreboard timers enabled? " + ChatColor.GREEN + ScoreboardHandler.scoreboardTimerEnabled);
    }

}