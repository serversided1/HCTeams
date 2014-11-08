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

        private String itemString;

        //***************************//

        public PVPDamage(String damaged, double damage, String damager, ItemStack itemStack) {
            super(damaged, damage, damager);
            this.itemString = "Error";

            if (itemStack.getType() == Material.AIR) {
                itemString = "their fists";
            } else {
                itemString = MobUtil.getItemName(itemStack);
            }
        }

        //***************************//

        public String getDescription() {
            return ("Killed by " + getDamager());
        }

        public String getDeathMessage() {
            return (ChatColor.RED + getDamaged() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamaged()) + "]" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + getDamager() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamager()) + "]" + ChatColor.YELLOW + " using " + ChatColor.RED + itemString + ChatColor.YELLOW + ".");
        }

        //***************************//

    }

    //***************************//

}
