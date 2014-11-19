package net.frozenorb.foxtrot.deathmessage.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.UnknownDamage;
import net.frozenorb.foxtrot.deathtracker.DeathTracker;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
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
            event2.getTrackerDamage().setHealthAfter(((Player) event.getEntity()).getHealthScale());
            DeathMessageHandler.addDamage((Player) event.getEntity(), event2.getTrackerDamage());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getDeathMessage() == null || event.getDeathMessage().isEmpty()) {
            return;
        }

        List<Damage> record = net.frozenorb.foxtrot.deathmessage.DeathMessageHandler.getDamage(event.getEntity());

        if (record == null || record.isEmpty()) {
            event.setDeathMessage(ChatColor.RED + event.getEntity().getName() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(event.getEntity().getName()) + "]" + ChatColor.YELLOW + " died.");
            return;
        }

        Damage deathCause = record.get(record.size() - 1);
        event.setDeathMessage(deathCause.getDeathMessage());

        // Hacky reflection to change the player's killer
        if (deathCause instanceof PlayerDamage) {
            Player killer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(((PlayerDamage) deathCause).getDamager());
            ((CraftPlayer) event.getEntity()).getHandle().killer = ((CraftPlayer) killer).getHandle();
        }

        DeathTracker.logDeath(event.getEntity(), event.getEntity().getKiller());
        net.frozenorb.foxtrot.deathmessage.DeathMessageHandler.clearDamage(event.getEntity());
    }

    //***************************//

}