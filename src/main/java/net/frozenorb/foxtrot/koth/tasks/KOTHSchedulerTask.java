package net.frozenorb.foxtrot.koth.tasks;

import net.frozenorb.foxtrot.FoxtrotPlugin;

import java.util.TimerTask;

/**
 * Created by macguy8 on 11/20/2014.
 */
public class KOTHSchedulerTask extends TimerTask {

    public void run() {
        (new KOTHSchedulerSyncTask()).runTask(FoxtrotPlugin.getInstance());
    }

}