package net.frozenorb.foxtrot.deathmessage.trackers;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.MobUtil;
import net.frozenorb.foxtrot.util.ClickableUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class PVPTracker implements Listener {

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

    public static class PVPDamage extends PlayerDamage {

        private String itemString;

        public PVPDamage(String damaged, double damage, String damager, ItemStack itemStack) {
            super(damaged, damage, damager);
            this.itemString = "Error";

            if (itemStack.getType() == Material.AIR) {
                itemString = "their fists";
            } else {
                itemString = MobUtil.getItemName(itemStack);
            }
        }

        public FancyMessage getDeathMessage() {
            FancyMessage deathMessage = ClickableUtils.deathMessageName(getDamaged());

            deathMessage.then(ChatColor.YELLOW + " was slain by ").then();
            ClickableUtils.appendDeathMessageName(getDamager(), deathMessage);
            deathMessage.then(ChatColor.YELLOW + " using " + ChatColor.RED + itemString + ChatColor.YELLOW + ".");

            return (deathMessage);
        }

    }

}