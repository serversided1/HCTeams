package net.frozenorb.foxtrot.damage.trackers;

import net.frozenorb.foxtrot.damage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.damage.objects.Damage;
import net.frozenorb.foxtrot.damage.objects.PlayerDamage;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.List;

/**
 * Created by macguy8 on 10/15/2014.
 */
public class VoidTracker implements Listener {

    //***************************//

    @EventHandler(priority=EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.VOID) {
            return;
        }

        List<Damage> record = net.frozenorb.foxtrot.damage.DeathMessageHandler.getDamage(event.getPlayer());
        Damage knocker = null;
        long knockerTime = 0L;

        if (record != null) {
            for (Damage damage : record) {
                if (damage instanceof VoidDamage || damage instanceof VoidDamageByPlayer) {
                    continue;
                }

                if (damage instanceof PlayerDamage && (knocker == null || damage.getTime() > knockerTime)) {
                    knocker = damage;
                    knockerTime = damage.getTime();
                }
            }
        }

        if (knocker != null) {
            event.setTrackerDamage(new VoidDamageByPlayer(event.getPlayer().getName(), event.getDamage(), ((PlayerDamage) knocker).getDamager(), !(knocker instanceof ArrowTracker.ArrowDamageByPlayer)));
        } else {
            event.setTrackerDamage(new VoidDamage(event.getPlayer().getName(), event.getDamage()));
        }
    }

    //***************************//

    public class VoidDamage extends Damage {

        //***************************//

        VoidDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        //***************************//

        public String getDescription() {
            return ("Void");
        }

        public String getDeathMessage() {
            return (ChatColor.GOLD + getDamaged() + ChatColor.RED + " fell into the void.");
        }

        //***************************//

    }

    //***************************//

    public class VoidDamageByPlayer extends PlayerDamage {

        //***************************//

        private boolean knocked;

        //***************************//

        VoidDamageByPlayer(String damaged, double damage, String damager, boolean knocked) {
            super(damaged, damage, damager);
            this.knocked = knocked;
        }

        //***************************//

        public String getDescription() {
            return ("Void");
        }

        public String getDeathMessage() {
            return (ChatColor.GOLD + getDamaged() + ChatColor.RED + " was " + (knocked ? "thrown" : "shot") + " out of the world by " + ChatColor.GOLD + getDamager() + ChatColor.RED + ".");
        }

        //***************************//

    }

    //***************************//

}