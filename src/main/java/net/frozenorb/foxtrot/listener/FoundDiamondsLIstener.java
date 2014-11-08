package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by macguy8 on 11/5/2014.
 */
public class FoundDiamondsListener implements Listener {

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.DIAMOND_ORE) {
            event.getBlock().setMetadata("DiamondPlaced", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.isCancelled() && event.getBlock().getType() == Material.DIAMOND_ORE && !event.getBlock().hasMetadata("DiamondPlaced")) {
            int diamonds = 0;

            for (int x = -5; x < 5; x++) {
                for (int y = -5; y < 5; y++) {
                    for (int z = -5; z < 5; z++) {
                        Block block = event.getBlock().getLocation().add(x, y, z).getBlock();

                        if (block.getType() == Material.DIAMOND_ORE && !block.hasMetadata("DiamondPlaced")) {
                            diamonds++;
                            block.setMetadata("DiamondPlaced", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));
                        }
                    }
                }
            }

            FoxtrotPlugin.getInstance().getServer().broadcastMessage(ChatColor.AQUA + event.getPlayer().getName() + " found " + diamonds + " diamond" + (diamonds == 1 ? "" : "s") + ".");
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onBlockBreak2(BlockBreakEvent event) {
        if (event.isCancelled() || (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH))) {
            return;
        }

        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                FoxtrotPlugin.getInstance().getDiamondMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getDiamondMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case GOLD_ORE:
                FoxtrotPlugin.getInstance().getGoldMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getGoldMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case IRON_ORE:
                FoxtrotPlugin.getInstance().getIronMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getIronMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case COAL_ORE:
                FoxtrotPlugin.getInstance().getCoalMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getCoalMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case REDSTONE_ORE:
            case GLOWING_REDSTONE_ORE:
                FoxtrotPlugin.getInstance().getRedstoneMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getRedstoneMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case LAPIS_ORE:
                FoxtrotPlugin.getInstance().getLapisMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getLapisMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
            case EMERALD_ORE:
                FoxtrotPlugin.getInstance().getEmeraldMinedMap().setMined(event.getPlayer().getName(), FoxtrotPlugin.getInstance().getEmeraldMinedMap().getMined(event.getPlayer().getName()) + 1);
                break;
        }
    }

}