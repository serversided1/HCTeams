package net.frozenorb.foxtrot.serialization.serializers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import net.frozenorb.Utilities.Core;
import net.frozenorb.foxtrot.serialization.JSONSerializer;

public class ItemStackSerializer implements JSONSerializer<ItemStack> {

	@Override
	public BasicDBObject serialize(ItemStack o) {
		if (o == null)
			return new BasicDBObject("type", "AIR").append("amount", 1).append("data", 0);
		BasicDBObject item = new BasicDBObject("type", o.getType().toString()).append("amount", Math.max(o.getAmount(), o.getMaxStackSize())).append("data", o.getDurability());
		BasicDBList enchants = new BasicDBList();
		for (Entry<Enchantment, Integer> entry : o.getEnchantments().entrySet()) {
			enchants.add(new BasicDBObject("enchantment", entry.getKey().getName()).append("level", entry.getValue()));
		}
		if (o.getEnchantments().size() > 0)
			item.append("enchants", enchants);
		if (o.hasItemMeta()) {
			ItemMeta m = o.getItemMeta();
			BasicDBObject meta = new BasicDBObject("displayName", m.getDisplayName());
			if (m.getLore() != null) {
				BasicDBList dblist = Core.get().getDatabaseRepresentation(m.getLore());
				meta.append("lore", dblist);
			}
			item.append("meta", meta);
		}
		return item;
	}

	@Override
	public ItemStack deserialize(BasicDBObject dbobj) {
		Material type = Material.valueOf(dbobj.getString("type"));
		ItemStack item = new ItemStack(type, dbobj.getInt("amount"));
		item.setDurability(Short.parseShort(dbobj.getString("data")));
		if (dbobj.containsField("enchants")) {
			BasicDBList enchs = (BasicDBList) dbobj.get("enchants");
			for (Object o : enchs) {
				BasicDBObject enchant = (BasicDBObject) o;
				item.addUnsafeEnchantment(Enchantment.getByName(enchant.getString("enchantment")), enchant.getInt("level"));
			}
		}
		if (dbobj.containsField("meta")) {
			BasicDBObject meta = (BasicDBObject) dbobj.get("meta");
			ItemMeta m = item.getItemMeta();
			if (meta.containsField("displayName")) {
				m.setDisplayName(meta.getString("displayName"));
			}
			if (meta.containsField("lore")) {
				final HashSet<String> lore = Core.get().getSetRepresentation((BasicDBList) meta.get("lore"));
				m.setLore(new ArrayList<String>() {
					private static final long serialVersionUID = -765088419932829612L;

					{
						addAll(lore);
					}
				});
			}
			item.setItemMeta(m);
		}
		return item;
	}
}
