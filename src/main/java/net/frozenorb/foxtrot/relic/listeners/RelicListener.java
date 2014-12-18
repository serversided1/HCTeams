package net.frozenorb.foxtrot.relic.listeners;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
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

public class RelicListener implements Listener {

    public RelicListener() {
        (new MinerRelicTask()).runTaskTimer(FoxtrotPlugin.getInstance(), 20L, 20L);
    }

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