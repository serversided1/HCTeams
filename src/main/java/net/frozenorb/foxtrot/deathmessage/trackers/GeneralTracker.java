package net.frozenorb.foxtrot.deathmessage.trackers;

import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GeneralTracker implements Listener {

    //***************************//

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        switch (event.getCause().getCause()) {
            case SUFFOCATION:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "Suffocation", "suffocated"));
                break;
            case DROWNING:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "Water", "drowned"));
                break;
            case STARVATION:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "Food", "starved to death"));
                break;
            case FIRE_TICK:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "Fire", "burned to death"));
                break;
            case LAVA:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "Lava", "burned to death"));
                break;
            case LIGHTNING:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "Lightning", "was struck by lightning"));
                break;
            case POISON:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "Poison", "was poisoned"));
                break;
            case WITHER:
                event.setTrackerDamage(new GeneralDamage(event.getPlayer().getName(), event.getDamage(), "Wither", "withered away"));
                break;
        }
    }

    //***************************//

    public class GeneralDamage extends Damage {

        //***************************//

        private String description;
        private String message;

        //***************************//

        public GeneralDamage(String damaged, double damage, String description, String message) {
            super(damaged, damage);

            this.description = description;
            this.message = message;
        }

        //***************************//

        public String getDescription() {
            return (description);
        }

        public String getDeathMessage() {
            return (ChatColor.GOLD + getDamaged() + ChatColor.RED + " " + message + ".");
        }

        //***************************//

    }

    //***************************//

}