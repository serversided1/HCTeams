package net.frozenorb.foxtrot.citadel.tasks;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CitadelSaveTask extends BukkitRunnable {

    public void run() {
        FoxtrotPlugin.getInstance().getCitadelHandler().saveCitadelInfo();
    }

}