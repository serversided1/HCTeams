package net.frozenorb.foxtrot.crates;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventorySerialization;
import org.bukkit.Bukkit;
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

public class CrateHandler implements Listener {

	@Getter private Map<String, Crate> crates;
	private DBCollection collection;

	public CrateHandler() {
		crates = new HashMap<>();

		Bukkit.getLogger().info("Creating indexes...");

		collection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("HCTCrates");
		collection.createIndex(new BasicDBObject("CrateName", 1));
		collection.createIndex(new BasicDBObject("Items", 1));

		Bukkit.getLogger().info("Creating indexes done.");

		// Load crates
		loadCrates();

		// Register this as a listener
		Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
	}

	public void loadCrates() {
		for (DBObject dbObject : collection.find()) {
			Crate crate = new Crate((String) dbObject.get("CrateName"));
			crate.setItems(InventorySerialization.deserialize((BasicDBList) dbObject.get("Items")));

			crates.put(crate.getKitName().toLowerCase(), crate);
		}
	}

	public void updateCrate(Player player, Crate crate) {
		crate.setItems(player.getInventory().getContents());

		BasicDBObject dbObject = new BasicDBObject();
		dbObject.put("CrateName", crate.getKitName());
		dbObject.put("Items", InventorySerialization.serialize(crate.getItems()));

		collection.update(new BasicDBObject("CrateName", crate.getKitName()), dbObject, true, false);
	}

	public void giveCrate(Player player, Crate crate) {
		for (ItemStack itemStack : crate.getItems()) {
			if (itemStack != null) {
				player.getInventory().addItem(itemStack);
			}
		}
	}

	@EventHandler
	public void onCrateInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			ItemStack inHand = player.getItemInHand();

			if (inHand.getType() == Material.ENDER_CHEST && inHand.hasItemMeta()) {
				String name = inHand.getItemMeta().getDisplayName();

				for (Crate crate : crates.values()) {
					if (name.equals(crate.getKitName())) {

						// Ensure player has enough free slots in their inventory to unzip the crate
						if (getFreeSlots(player.getInventory()) >= (crate.getSize() - 1)) {
							if (inHand.getAmount() > 1) {
								inHand.setAmount(inHand.getAmount() - 1);
							} else {
								player.getInventory().remove(inHand); // use create
							}

							// unzip fully enchanted set into players inventory
							for (ItemStack is : crate.getItems()) {
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

			for (Crate crate : crates.values()) {
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
