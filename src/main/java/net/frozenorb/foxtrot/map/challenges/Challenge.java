package net.frozenorb.foxtrot.map.challenges;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class Challenge implements Listener {

	@Getter private String name;
	@Getter private String description;
	@Getter private ItemStack icon;
	@Getter private int countToQualify;

	public Challenge(String name, String description, ItemStack icon, int countToQualify) {
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.countToQualify = countToQualify;

		Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
	}

	public boolean qualifies(PlayerInventory armor, String armorContains) {
		return (armor.getHelmet() != null &&
		        armor.getChestplate() != null &&
		        armor.getLeggings() != null &&
		        armor.getBoots() != null &&
		        armor.getHelmet().getType().name().startsWith(armorContains) &&
		        armor.getChestplate().getType().name().startsWith(armorContains) &&
		        armor.getLeggings().getType().name().startsWith(armorContains) &&
		        armor.getBoots().getType().name().startsWith(armorContains));
	}

	public String getMongoName() {
		return name.replaceAll(" ", "_");
	}

}
