package net.frozenorb.foxtrot.relic.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.relic.enums.Relic;
import net.frozenorb.foxtrot.relic.tasks.MinerRelicTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class RelicListener implements Listener {

    private Map<String, Long> lastLifesteal = new HashMap<String, Long>();

    public RelicListener() {
        (new MinerRelicTask()).runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (FoxtrotPlugin.getInstance().getRelicHandler().getTier((Player) event.getEntity(), Relic.FOOD_LOCK) != -1) {
            if (event.getFoodLevel() < ((Player) event.getEntity()).getFoodLevel()) {
                event.setFoodLevel(20);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            int tier = FoxtrotPlugin.getInstance().getRelicHandler().getTier(damager, Relic.LIFESTEAL);

            if (tier != -1) {
                if (lastLifesteal.containsKey(damager.getName()) && lastLifesteal.get(damager.getName()) + 1000 > System.currentTimeMillis()) {
                    return;
                }

                float chance = 0F;

                switch (tier) {
                    case 1:
                        chance = 0.05F;
                        break;
                    case 2:
                        chance = 0.10F;
                        break;
                    case 3:
                        chance = 0.10F;
                        break;
                }

                if (FoxtrotPlugin.RANDOM.nextFloat() < chance) {
                    damager.setHealth(Math.min(damager.getHealth() + (tier == 3 ? 2 : 1), damager.getMaxHealth()));
                    ((Player) event.getEntity()).sendMessage(ChatColor.AQUA + damager.getName() + "'s life steal relic has healed them!");
                    damager.sendMessage(ChatColor.AQUA + "Your lifesteal relic has healed you!");
                    lastLifesteal.put(damager.getName(), System.currentTimeMillis());
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player  = (Player) event.getEntity();

        if (!MinerRelicTask.getMinerInvisWarmup().containsKey(player.getName()) || MinerRelicTask.getMinerInvisWarmup().get(player.getName()) != -1) {
            return;
        }

        MinerRelicTask.getMinerLastDamage().put(player.getName(), 10);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been temporarily removed!");
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getDamager();

        if (!MinerRelicTask.getMinerInvisWarmup().containsKey(player.getName()) || MinerRelicTask.getMinerInvisWarmup().get(player.getName()) != -1) {
            return;
        }

        MinerRelicTask.getMinerLastDamage().put(player.getName(), 10);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.sendMessage(ChatColor.BLUE + "Miner Invisibility" + ChatColor.YELLOW + " has been temporarily removed!");
    }

}