package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class MapListener implements Listener {

    private void startUpdate(final Furnace tile, final int increase) {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (tile.getCookTime() > 0 || tile.getBurnTime() > 0) {
                    tile.setCookTime((short) (tile.getCookTime() + increase));
                    tile.update();
                } else {
                    cancel();
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 1L, 1L);
    }

    // ALPHA
    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event){
        Furnace tile = (Furnace) event.getBlock().getState();
        // Averages to 1.5
        startUpdate(tile, FoxtrotPlugin.RANDOM.nextBoolean() ? 1 : 2);
    }

    /*@EventHandler(priority= EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getPlayer().getItemInHand() == null || !event.getPlayer().getItemInHand().getType().name().contains("PICKAXE") || event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }

        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                for (int i = 0; i < 4; i++) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.DIAMOND));
                }

                break;
            case GOLD_ORE:
                for (int i = 0; i < 4; i++) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT));
                }

                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                break;
            case IRON_ORE:
                for (int i = 0; i < 4; i++) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));
                }

                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                break;
            case GLOWING_REDSTONE_ORE:
            case REDSTONE_ORE:
                for (int i = 0; i < 20; i++) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.REDSTONE));
                }

                break;
            case EMERALD_ORE:
                for (int i = 0; i < 4; i++) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.EMERALD));
                }

                break;
            case COAL_ORE:
                for (int i = 0; i < 6; i++) {
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.COAL));
                }

                break;
        }
    }*/

}