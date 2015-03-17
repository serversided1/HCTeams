package net.frozenorb.foxtrot.teamactiontracker;

import com.google.common.io.Files;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.teamactiontracker.enums.TeamActionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TeamActionTracker {

    public static void logActionAsync(final Team team, final TeamActionType actionType, final String message) {
        new BukkitRunnable() {

            public void run() {
                logAction(team, actionType, message);
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    public static void logAction(Team team, TeamActionType actionType, String message) {
        // If the team is still being setup this will happen.
        if (team == null || team.isLoading()) {
            return;
        }

        File logToFolder = new File("foxlogs" + File.separator + "teamactiontracker" + File.separator + team.getName());
        File logTo = new File(logToFolder, actionType.getName().toLowerCase() + ".log");

        try {
            logTo.getParentFile().mkdirs();
            logTo.createNewFile();

            Files.append("[" + SimpleDateFormat.getDateTimeInstance().format(new Date()) + "] " + message + "\n", logTo, Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}