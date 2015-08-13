package net.frozenorb.foxtrot.chat.tasks;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.scheduler.BukkitRunnable;

public class SaveCustomPrefixesTask extends BukkitRunnable {

    public void run() {
        Foxtrot.getInstance().getChatHandler().saveCustomPrefixes();
    }

}