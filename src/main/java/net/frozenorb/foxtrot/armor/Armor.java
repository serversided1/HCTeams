package net.frozenorb.foxtrot.armor;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class Armor {

	@Getter @NonNull ItemStack[] items;

	public boolean isFullSet(ArmorMaterial material) {

		boolean is = true;
		for (ItemStack item : items) {
			if (item == null || item.getType() == Material.AIR) {
				return false;
			}
			if (!item.getType().name().contains(material.name())) {
				return false;
			}

		}

		return is;

	}

	public static Armor of(Player p) {
		Armor a = new Armor(p.getInventory().getArmorContents());

		return a;
	}
}
