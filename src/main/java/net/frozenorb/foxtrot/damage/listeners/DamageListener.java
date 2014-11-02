package net.frozenorb.foxtrot.damage.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.damage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.damage.objects.Damage;
import net.frozenorb.foxtrot.damage.util.UnknownDamage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

/**
 * Created by macguy8 on 10/3/2014.
 */
public class DamageListener implements Listener {

    //***************************//

    @EventHandler(ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        CustomPlayerDamageEvent event2 = new CustomPlayerDamageEvent(event);

        event2.setTrackerDamage(new UnknownDamage(((Player) event.getEntity()).getName(), event.getDamage()));

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(event2);

        if (event2.isCancelled()) {
            event.setCancelled(true);
        } else {
            net.frozenorb.foxtrot.damage.DeathMessageHandler.addDamage((Player) event.getEntity(), event2.getTrackerDamage());
        }
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getDeathMessage() == null || event.getDeathMessage().isEmpty()) {
            return;
        }

        List<Damage> record = net.frozenorb.foxtrot.damage.DeathMessageHandler.getDamage(event.getEntity());

        if (record == null || record.isEmpty()) {
            event.setDeathMessage(ChatColor.GOLD + event.getEntity().getName() + ChatColor.RED + " died.");
            return;
        }

        Damage deathCause = record.get(record.size() - 1);
        event.setDeathMessage(deathCause.getDeathMessage());

        net.frozenorb.foxtrot.damage.DeathMessageHandler.clearDamage(event.getEntity());
    }

    //***************************//

}