package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ExperienceRatesTeamUpgrade implements TeamUpgrade {

	@Override
	public String getUpgradeName() {
		return "Experience Rates";
	}

	@Override
	public String getDescription() {
		return "Increase the amount of XP earned in your claim";
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.EXP_BOTTLE);
	}

	@Override
	public int getPrice(int tier) {
		switch (tier) {
			case 1:
				return 25;
			case 2:
				return 50;
			case 3:
				return 75;
			default:
				throw new RuntimeException("This shouldn't happen");
		}
	}

	@Override
	public int getTierLimit() {
		return 3;
	}

}
