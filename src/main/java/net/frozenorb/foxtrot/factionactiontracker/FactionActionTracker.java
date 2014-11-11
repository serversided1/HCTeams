package net.frozenorb.foxtrot.factionactiontracker;

import net.frozenorb.foxtrot.team.Team;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

/**
 * Created by macguy8 on 11/6/2014.
 */
public class FactionActionTracker {

    public static void logAction(Team team, String category, String message) {
        // If the team is still being setup this might happen.
        if (team == null || team.isLoading()) {
            return;
        }

        File logToFolder = new File("factionactiontracker" + File.separator + team.getFriendlyName());
        File logTo = new File(logToFolder, category + ".log");

        try {
            logTo.getParentFile().mkdirs();
            logTo.createNewFile();

            BufferedWriter output = new BufferedWriter(new FileWriter(logTo, true));
            output.append("[").append(new Date().toString()).append("] ").append(message).append("\n");
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}