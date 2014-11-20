package net.frozenorb.foxtrot.koth.tasks;

import net.frozenorb.foxtrot.koth.KOTHHandler;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by macguy8 on 11/20/2014.
 */
public class KOTHSchedulerSyncTask extends BukkitRunnable {

    public void run() {
        KOTHHandler.onSchedulerTick();
    }

}