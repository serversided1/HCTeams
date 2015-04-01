package net.frozenorb.foxtrot.deathmessage.listeners;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.UnknownDamage;
import net.frozenorb.foxtrot.deathtracker.DeathTracker;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class DamageListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player.getName(), event.getDamage()));

            Foxtrot.getInstance().getServer().getPluginManager().callEvent(customEvent);
            DeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());

        if (record != null) {
            Damage deathCause = record.get(record.size() - 1);

            // Hacky NMS to change the player's killer
            if (deathCause instanceof PlayerDamage) {
                String killerName = ((PlayerDamage) deathCause).getDamager();
                Player killer = Foxtrot.getInstance().getServer().getPlayerExact(killerName);

                if (killer != null) {
                    ((CraftPlayer) event.getEntity()).getHandle().killer = ((CraftPlayer) killer).getHandle();
                    Foxtrot.getInstance().getKillsMap().setKills(killer.getUniqueId(), Foxtrot.getInstance().getKillsMap().getKills(killer.getUniqueId()) + 1);
                }
            }

            event.setDeathMessage(deathCause.getDeathMessage());
        } else {
            event.setDeathMessage((new UnknownDamage(event.getEntity().getName(), 1)).getDeathMessage());
        }

        DeathTracker.logDeath(event.getEntity(), event.getEntity().getKiller());
        DeathMessageHandler.clearDamage(event.getEntity());
    }

}