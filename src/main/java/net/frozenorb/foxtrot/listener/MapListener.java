package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.server.v1_7_R4.AttributeInstance;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHorse;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

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

    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true) // This is actually 'Lowest', but Bukkit calls listeners LOWEST -> HIGHEST, so HIGHEST is what's actually called last. #BukkitBeLike
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getItemInHand() == null || !event.getPlayer().getItemInHand().getType().name().contains("PICKAXE") || event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        Material blockType = event.getBlock().getType();

        if (blockType == Material.GOLD_ORE || blockType == Material.IRON_ORE) {
            ItemStack drop;

            if (blockType == Material.GOLD_ORE) {
                drop = new ItemStack(Material.GOLD_INGOT);
            } else {
                drop = new ItemStack(Material.IRON_INGOT);
            }

            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
            event.setCancelled(true);
            event.getPlayer().giveExp(4);
            event.getBlock().setType(Material.AIR);
        }
    }

}