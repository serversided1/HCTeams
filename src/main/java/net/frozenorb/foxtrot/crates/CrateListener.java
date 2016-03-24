package net.frozenorb.foxtrot.crates;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CrateListener implements Listener {

    @EventHandler
    public void onCrateInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            ItemStack inHand = player.getItemInHand();

            if (inHand.getType() == Material.ENDER_CHEST && inHand.hasItemMeta()) {
                String name = inHand.getItemMeta().getDisplayName();

                for (Crate crate : Crate.values()) {
                    if (name.equals(crate.getKitName())) {

                        // Ensure player has enough free slots in their inventory to unzip the crate
                        if(getFreeSlots(player.getInventory()) >= (crate.getSize() - 1)) {
                            player.getInventory().remove(inHand); // use create

                            // unzip fully enchanted set into players inventory
                            for(ItemStack is : crate.getInventory()) {
                                player.getInventory().addItem(is);
                            }

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    player.updateInventory();
                                }
                            }.runTaskLater(Foxtrot.getInstance(), 1L);

                        } else {
                            player.sendMessage(ChatColor.RED + "You dont have enough space in your inventory!");
                        }

                        event.setCancelled(true); // cancel interact
                        event.setUseInteractedBlock(Event.Result.DENY); // cancel place
                        event.setUseItemInHand(Event.Result.DENY); // cancel use
                        return; // we're done looping
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCratePlace(BlockPlaceEvent event) {
        if (event.getItemInHand().getType() == Material.ENDER_CHEST && event.getItemInHand().hasItemMeta()) {
            String name = event.getItemInHand().getItemMeta().getDisplayName();

            for (Crate crate : Crate.values()) {
                if (name.equals(crate.getKitName())) {
                    event.setCancelled(true); // to be certain the enderchest cannot be placed if it's a crate
                    return;
                }
            }
        }
    }

    private int getFreeSlots(Inventory inventory) {
        int free = 0;

        for (ItemStack is : inventory.getContents()) {
            if (is == null || is.getType() == Material.AIR) {
                free++;
            }
        }

        return free;
    }
}
