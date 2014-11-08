package net.frozenorb.foxtrot.deathmessage.trackers;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
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

        List<Damage> record = net.frozenorb.foxtrot.deathmessage.DeathMessageHandler.getDamage(event.getPlayer());
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
            return (ChatColor.RED + getDamaged() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamaged()) + "]" + ChatColor.YELLOW + " fell into the void.");
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
            return (ChatColor.RED + getDamaged() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamaged()) + "]" + ChatColor.YELLOW + " fell into the void thanks to " + ChatColor.RED + getDamager() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamager()) + "]" + ChatColor.YELLOW + ".");
        }

        //***************************//

    }

    //***************************//

}