package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ExtraSpawnerTeamUpgrade implements TeamUpgrade {

	@Override
	public String getUpgradeName() {
		return "Extra Spawner";
	}

	@Override
	public String getDescription() {
		return "Increase the maximum amount of spawners you can have in your claim";
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.MOB_SPAWNER);
	}

	@Override
	public int getPrice(int tier) {
		return 15 + ((tier - 1) * 15);
	}

	@Override
	public int getTierLimit() {
		return 5;
	}

}
