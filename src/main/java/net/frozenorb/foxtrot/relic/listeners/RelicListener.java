package net.frozenorb.foxtrot.relic.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.relic.enums.Relic;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class RelicListener implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getRelicHandler().getTier((Player) event.getEntity(), Relic.FOOD_LOCK) != -1) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            int tier = FoxtrotPlugin.getInstance().getRelicHandler().getTier(damager, Relic.LIFESTEAL);

            if (tier != -1) {
                float chance = 0F;

                switch (tier) {
                    case 1:
                        chance = 0.05F;
                        break;
                    case 2:
                        chance = 0.10F;
                        break;
                    case 3:
                        chance = 0.20F;
                        break;
                }

                if (FoxtrotPlugin.RANDOM.nextFloat() < chance) {
                    damager.setHealth(Math.min(damager.getHealth() + 1, damager.getMaxHealth()));
                    damager.sendMessage(ChatColor.AQUA + "Your lifesteal relic has healed you!");
                }
            }
        }
    }

}