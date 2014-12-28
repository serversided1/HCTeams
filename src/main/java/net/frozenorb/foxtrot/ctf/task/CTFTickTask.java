package net.frozenorb.foxtrot.ctf.task;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CTFTickTask extends BukkitRunnable {

    public void run() {
        if (FoxtrotPlugin.getInstance().getCTFHandler().getGame() != null) {
            FoxtrotPlugin.getInstance().getCTFHandler().getGame().tick();
        }
    }

}