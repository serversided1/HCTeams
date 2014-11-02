package net.frozenorb.foxtrot.deathmessage.trackers;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.MobDamage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ArrowTracker implements Listener {

    //***************************//

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        event.getProjectile().setMetadata("ShotFromDistance", new FixedMetadataValue(FoxtrotPlugin.getInstance(), event.getProjectile().getLocation()));
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();

            if (e.getDamager() instanceof Arrow) {
                Arrow a = (Arrow) e.getDamager();

                if (a.getShooter() instanceof Player) {
                    Player shooter = (Player) a.getShooter();

                    for (MetadataValue value : a.getMetadata("ShotFrom")) {
                        double distance = ((Location) value.value()).distance(event.getPlayer().getLocation());

                        event.setTrackerDamage(new ArrowDamageByPlayer(event.getPlayer().getName(), event.getDamage(), shooter.getName(), distance));
                    }
                } else if (a.getShooter() instanceof Entity) {
                    Entity shooter = (Entity) a.getShooter();
                    event.setTrackerDamage(new ArrowDamageByMob(event.getPlayer().getName(), event.getDamage(), shooter));
                } else {
                    event.setTrackerDamage(new ArrowDamage(event.getPlayer().getName(), event.getDamage()));
                }
            }
        }
    }

    //***************************//

    public class ArrowDamage extends Damage {

        //***************************//

        public ArrowDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        //***************************//

        public String getDescription() {
            return ("Shot");
        }

        public String getDeathMessage() {
            return (ChatColor.GOLD + getDamaged() + ChatColor.RED + " was shot.");
        }

        //***************************//

    }

    public class ArrowDamageByPlayer extends PlayerDamage {

        //***************************//

        double distance;

        //***************************//

        public ArrowDamageByPlayer(String damaged, double damage, String damager, double distance) {
            super(damaged, damage, damager);
            this.distance = distance;
        }

        //***************************//

        public String getDescription() {
            return ("Shot by " + getDamager());
        }

        public String getDeathMessage() {
            return (ChatColor.GOLD + getDamaged() + ChatColor.RED + " was shot by " + ChatColor.GOLD + getDamager() + ChatColor.RED + " from " + Math.round(distance) + " blocks.");
        }

        public double getDistance() {
            return (distance);
        }

        //***************************//

    }

    public class ArrowDamageByMob extends MobDamage {

        //***************************//

        public ArrowDamageByMob(String damaged, double damage, Entity damager) {
            super(damaged, damage, damager.getType());
        }

        //***************************//

        public String getDescription() {
            return ("Shot by " + getMobType().getName());
        }

        public String getDeathMessage() {
            return (ChatColor.GOLD + getDamaged() + ChatColor.RED + " was shot by a " + ChatColor.GOLD + getMobType().getName() + ChatColor.RED + ".");
        }

        //***************************//

    }

    //***************************//

}