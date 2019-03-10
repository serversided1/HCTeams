package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BrokenKeyListener implements Listener {

	// thanks Aabis -joeleoli
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Chest && event.getItem() != null && event.getItem().getType() == Material.TRIPWIRE_HOOK) {
			ItemStack itemStack = event.getItem();
			ItemMeta itemMeta = itemStack.getItemMeta();

			if (itemMeta.hasDisplayName() && itemMeta.hasLore()) {
				if (itemMeta.getDisplayName().contains("Error")) {
					for (String line : itemMeta.getLore()) {
						if (line.contains(ChatColor.GREEN.toString()) && line.contains("Check the Crate: ")) {
							String[] split = line.split("Check the Crate: ");
							String keyName = split[1];

							event.getPlayer().setItemInHand(null);
							event.getPlayer().updateInventory();

							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + event.getPlayer().getName() + " " + keyName + " " + itemStack.getAmount());
							Foxtrot.getInstance().getLogger().info("Replacing broken keys for " + event.getPlayer().getName() + ": " + keyName + " " + itemStack.getAmount());

							break;
						}
					}
				} else if (itemMeta.getDisplayName().contains("[") && itemMeta.getDisplayName().contains(ChatColor.GOLD + "Bounty") && itemMeta.getDisplayName().contains(ChatColor.GRAY + "Crate")) {
					event.getPlayer().setItemInHand(null);
					event.getPlayer().updateInventory();

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + event.getPlayer().getName() + " bounty " + itemStack.getAmount());
					Foxtrot.getInstance().getLogger().info("Replacing broken keys for " + event.getPlayer().getName() + ": bounty " + itemStack.getAmount());
				}
			}
		}
	}

}
