package net.frozenorb.foxtrot.teamactiontracker;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class TeamActionTracker {

    public static void logAction(Team team, TeamActionType actionType, String message) {
        // If the team is still being setup this might happen.
        if (team == null || team.isLoading()) {
            return;
        }

        File logToFolder = new File("foxlogs" + File.separator + "teamactiontracker" + File.separator + team.getName());
        File logTo = new File(logToFolder, actionType.name().toLowerCase() + ".log");

        try {
            logTo.getParentFile().mkdirs();
            logTo.createNewFile();

            BufferedWriter output = new BufferedWriter(new FileWriter(logTo, true));
            output.append("[").append(SimpleDateFormat.getDateTimeInstance().format(new Date())).append("] ").append(message).append("\n");
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}