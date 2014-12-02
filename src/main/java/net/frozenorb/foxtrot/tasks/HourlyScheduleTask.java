package net.frozenorb.foxtrot.tasks;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.events.HourEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created by macguy8 on 12/2/2014.
 */
public class HourlyScheduleTask extends TimerTask {

    public void run() {
        new BukkitRunnable() {

            public void run() {
                FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new HourEvent(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
            }

        }.runTask(FoxtrotPlugin.getInstance());
    }

}