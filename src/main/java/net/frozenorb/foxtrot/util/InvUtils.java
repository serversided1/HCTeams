package net.frozenorb.foxtrot.util;

import java.util.Map.Entry;

import net.frozenorb.foxtrot.FoxtrotPlugin;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class InvUtils {

	public static void fixItem(ItemStack item) {
		for (Entry<Enchantment, Integer> entry : FoxtrotPlugin.getInstance().getServerManager().getMaxEnchantments().entrySet()) {

			if (item.containsEnchantment(entry.getKey()) && item.getEnchantmentLevel(entry.getKey()) > entry.getValue()) {
				if (entry.getValue() == -1) {
					item.addEnchantment(Enchantment.DURABILITY, entry.getValue());
				} else {
					item.addEnchantment(entry.getKey(), entry.getValue());
				}
			}
		}

	}
}
