package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.qLib;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MapListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        double multiplier = Foxtrot.getInstance().getMapHandler().getBaseLootingMultiplier();

        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();

            if (player.getItemInHand() != null && player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS)) {
                switch (player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)) {
                    case 1:
                        multiplier = Foxtrot.getInstance().getMapHandler().getLevel1LootingMultiplier();
                        break;
                    case 2:
                        multiplier = Foxtrot.getInstance().getMapHandler().getLevel2LootingMultiplier();
                        break;
                    case 3:
                        multiplier = Foxtrot.getInstance().getMapHandler().getLevel3LootingMultiplier();
                        break;
                    default:
                        break;
                }
            }
        }

        event.setDroppedExp((int) Math.ceil(event.getDroppedExp() * multiplier));
    }

}