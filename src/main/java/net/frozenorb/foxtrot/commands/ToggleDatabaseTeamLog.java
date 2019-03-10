package net.frozenorb.foxtrot.commands;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.qlib.command.Command;

public class ToggleDatabaseTeamLog {

    @Command(names = {"toggledatabaseteamlog" }, permission = "op")
    public static void toggleDatabaseTeamLog(Player sender) {
        TeamActionTracker.setDatabaseLogEnabled(!TeamActionTracker.isDatabaseLogEnabled());
        sender.sendMessage("Enabled: " + TeamActionTracker.isDatabaseLogEnabled());
    }

}