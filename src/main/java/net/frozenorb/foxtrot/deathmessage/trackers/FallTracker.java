package net.frozenorb.foxtrot.deathmessage.trackers;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.util.ClickableUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

public class FallTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        List<Damage> record = DeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;

        if (record != null) {
            for (Damage damage : record) {
                if (damage instanceof FallDamage || damage instanceof FallDamageByPlayer) {
                    continue;
                }

                if (damage instanceof PlayerDamage && (knocker == null || damage.getTime() > knockerTime)) {
                    knocker = damage;
                    knockerTime = damage.getTime();
                }
            }
        }

        if (knocker != null) {
            event.setTrackerDamage(new FallDamageByPlayer(event.getPlayer().getName(), event.getDamage(), ((PlayerDamage) knocker).getDamager()));
        } else {
            event.setTrackerDamage(new FallDamage(event.getPlayer().getName(), event.getDamage()));
        }
    }

    public static class FallDamage extends Damage {

        public FallDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        public FancyMessage getDeathMessage() {
            return (ClickableUtils.deathMessageName(getDamaged()).then(ChatColor.YELLOW + " hit the ground too hard."));
        }

    }

    public static class FallDamageByPlayer extends PlayerDamage {

        public FallDamageByPlayer(String damaged, double damage, String damager) {
            super(damaged, damage, damager);
        }

        public FancyMessage getDeathMessage() {
            FancyMessage deathMessage = ClickableUtils.deathMessageName(getDamaged());

            deathMessage.then(ChatColor.YELLOW + " hit the ground too hard thanks to ").then();
            ClickableUtils.appendDeathMessageName(getDamager(), deathMessage);
            deathMessage.then(ChatColor.YELLOW + ".");

            return (deathMessage);
        }

    }

}