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

public class FallTracker implements Listener {

    //***************************//

    @EventHandler(priority=EventPriority.LOW)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause().getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        List<Damage> record = net.frozenorb.foxtrot.deathmessage.DeathMessageHandler.getDamage(event.getPlayer());
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
            event.setTrackerDamage(new FallDamageByPlayer(event.getPlayer().getName(), event.getDamage(), ((PlayerDamage) knocker).getDamager(), Math.round(event.getPlayer().getFallDistance())));
        } else {
            event.setTrackerDamage(new FallDamage(event.getPlayer().getName(), event.getDamage(), Math.round(event.getPlayer().getFallDistance())));
        }
    }

    //***************************//

    public class FallDamage extends Damage {

        //***************************//

        private int distance;

        //***************************//

        public FallDamage(String damaged, double damage, int distance) {
            super(damaged, damage);

            this.distance = distance;
        }

        //***************************//

        public String getDescription() {
            if (distance == 0) {
                return ("Enderpearl");
            } else {
                return ("Fall (" + distance + " block" + (distance == 1 ? "" : "s") + ")");
            }
        }

        public String getDeathMessage() {
            return (ChatColor.RED + getDamaged() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamaged()) + "] " + ChatColor.YELLOW + "hit the ground too hard.");
        }

        //***************************//

    }

    //***************************//

    public class FallDamageByPlayer extends PlayerDamage {

        //***************************//

        private int distance;

        //***************************//

        public FallDamageByPlayer(String damaged, double damage, String damager, int distance) {
            super(damaged, damage, damager);

            this.distance = distance;
        }

        //***************************//

        public String getDescription() {
            if (distance == 0) {
                return ("Enderpearl");
            } else {
                return ("Fall (" + distance + " block" + (distance == 1 ? "" : "s") + ")");
            }
        }

        public String getDeathMessage() {
            return (ChatColor.RED + getDamaged() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamaged()) + "] " + ChatColor.YELLOW + "hit the ground too hard thanks to " + ChatColor.RED + getDamager() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamager()) + "]" + ChatColor.YELLOW + ".");
        }

        //***************************//

    }

    //***************************//

}