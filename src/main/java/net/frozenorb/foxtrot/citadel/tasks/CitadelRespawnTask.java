package net.frozenorb.foxtrot.citadel.tasks;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by macguy8 on 11/25/2014.
 */
public class CitadelRespawnTask extends BukkitRunnable {

    public void run() {
        FoxtrotPlugin.getInstance().getCitadelHandler().respawnCitadelChests();
    }

}