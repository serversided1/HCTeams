package net.frozenorb.foxtrot.deathmessage.trackers;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.MobUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Tracker to detect PVP damage.
 */
public class PVPTracker implements Listener {

    //***************************//

    public static String[] DEATH_VERBS = new String[] { "rekt", "destroyed", "shanked", "shrekt", "killed", "obliterated" };

    //***************************//

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();

            if (e.getDamager() instanceof Player) {
                Player damager = (Player) e.getDamager();
                Player damaged = event.getPlayer();

                event.setTrackerDamage(new PVPDamage(damaged.getName(), event.getDamage(), damager.getName(), damager.getItemInHand()));
            }
        }
    }

    //***************************//

    public class PVPDamage extends PlayerDamage {

        //***************************//

        private String item;

        //***************************//

        public PVPDamage(String damaged, double damage, String damager, ItemStack itemStack) {
            super(damaged, damage, damager);
            this.item = (itemStack.getType() == Material.AIR ? "their fists" : "a " + MobUtil.getItemName(itemStack));
        }

        //***************************//

        public String getDescription() {
            return ("Killed by " + getDamager());
        }

        public String getDeathMessage() {
            return (ChatColor.GOLD + getDamaged() + ChatColor.RED + " was " + DEATH_VERBS[FoxtrotPlugin.RANDOM.nextInt(DEATH_VERBS.length)] + " by " + ChatColor.GOLD + getDamager() + ChatColor.RED + " using " + item + ChatColor.RED + ".");
        }

        //***************************//

    }

    //***************************//

}
