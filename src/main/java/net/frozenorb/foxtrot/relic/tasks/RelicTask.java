package net.frozenorb.foxtrot.relic.tasks;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RelicTask extends BukkitRunnable {

  public void run() {
      for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
          FoxtrotPlugin.getInstance().getRelicHandler().updatePlayer(player);
      }
  }

}