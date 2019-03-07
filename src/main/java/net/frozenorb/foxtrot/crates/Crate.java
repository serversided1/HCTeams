package net.frozenorb.foxtrot.crates;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

@Getter
public class Crate {

	private final String kitName;
	@Setter private ItemStack[] items;

	public Crate(String kitName) {
		this(kitName, new ItemStack[36]);
	}

	public Crate(String kitName, ItemStack[] items) {
		this.kitName = kitName;
		this.items = items;
	}

	public List<String> getLore() {
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.DARK_PURPLE + "Right click to open this " + kitName + ChatColor.DARK_PURPLE + " crate.");
		lore.add("");
		lore.add(ChatColor.YELLOW + "Crate requires " + ChatColor.DARK_GRAY + getSize() + ChatColor.YELLOW + " empty slots to open.");
		return lore;
	}

	public int getSize() {
		return items.length;
	}

}
