package net.frozenorb.foxtrot.deathmessage.listeners;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.UnknownDamage;
import net.frozenorb.foxtrot.deathtracker.DeathTracker;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
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

            FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(customEvent);
            DeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());
        FancyMessage deathMessage;

        if (record != null) {
            Damage deathCause = record.get(record.size() - 1);

            // Hacky NMS to change the player's killer
            if (deathCause instanceof PlayerDamage) {
                String killerName = ((PlayerDamage) deathCause).getDamager();
                Player killer = FoxtrotPlugin.getInstance().getServer().getPlayerExact(killerName);

                if (killer != null) {
                    ((CraftPlayer) event.getEntity()).getHandle().killer = ((CraftPlayer) killer).getHandle();
                }

                // TODO: Should this be here?
                FoxtrotPlugin.getInstance().getKillsMap().setKills(killerName, FoxtrotPlugin.getInstance().getKillsMap().getKills(killerName) + 1);
            }

            deathMessage = deathCause.getDeathMessage();
        } else {
            deathMessage = (new UnknownDamage(event.getEntity().getName(), 1)).getDeathMessage();
        }

        // Use our custom clickable deathmessage
        event.setDeathMessage(null);
        deathMessage.send(FoxtrotPlugin.getInstance().getServer().getOnlinePlayers());
        deathMessage.send(FoxtrotPlugin.getInstance().getServer().getConsoleSender());

        DeathTracker.logDeath(event.getEntity(), event.getEntity().getKiller());
        DeathMessageHandler.clearDamage(event.getEntity());
    }

}