package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.teamactiontracker.TeamActionTracker;
import net.frozenorb.qlib.command.Command;

import org.bukkit.entity.Player;

public class ToggleDatabaseTeamLog {

    @Command(names = {"toggledatabaseteamlog" }, permission = "op")
    public static void toggleDatabaseTeamLog(Player sender) {
        TeamActionTracker.setDatabaseLogEnabled(!TeamActionTracker.isDatabaseLogEnabled());
        sender.sendMessage("Enabled: " + TeamActionTracker.isDatabaseLogEnabled());
    }

}