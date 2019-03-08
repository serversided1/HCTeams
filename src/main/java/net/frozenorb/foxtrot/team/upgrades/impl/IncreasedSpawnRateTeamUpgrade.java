package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class IncreasedSpawnRateTeamUpgrade implements TeamUpgrade {

	@Override
	public String getUpgradeName() {
		return "Increase Mob Spawn Rate";
	}

	@Override
	public String getDescription() {
		return "Increase the rate at which mobs spawn in your claim";
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.SKULL_ITEM);
	}

	@Override
	public int getPrice(int tier) {
		switch (tier) {
			case 1:
				return 15;
			case 2:
				return 35;
			case 3:
				return 50;
			default:
				throw new RuntimeException("This shouldn't happen");
		}
	}

	@Override
	public int getTierLimit() {
		return 3;
	}

}
