package net.frozenorb.foxtrot.serialization.serializers;

import java.util.Map.Entry;

import net.frozenorb.foxtrot.serialization.JSONSerializer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mongodb.BasicDBObject;

public class InventorySerializer implements JSONSerializer<Inventory> {

	@Override
	public BasicDBObject serialize(Inventory o) {
		BasicDBObject full = new BasicDBObject();
		BasicDBObject inv = new BasicDBObject();
		for (int i = 0; i < o.getSize(); i += 1) {
			ItemStack item = o.getItem(i);
			if (item != null && item.getType() != Material.AIR) {
				inv.put(i + "", new ItemStackSerializer().serialize(item));
			}
		}
		full.put("inventory", inv);
		return full;
	}

	@Override
	public Inventory deserialize(BasicDBObject dbobj) {
		Inventory inv = Bukkit.createInventory(null, 36);
		BasicDBObject contents = (BasicDBObject) dbobj.get("inventory");
		for (Entry<String, Object> str : contents.entrySet()) {
			int slot = Integer.parseInt(str.getKey());
			ItemStack item = new ItemStackSerializer().deserialize((BasicDBObject) str.getValue());

			if (item.getAmount() == 0) {
				item.setAmount(1);
			}

			inv.setItem(slot, item);
		}
		return inv;
	}
}
