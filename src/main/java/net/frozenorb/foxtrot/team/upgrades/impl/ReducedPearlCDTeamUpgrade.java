package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ReducedPearlCDTeamUpgrade implements TeamUpgrade {

	@Override
	public String getUpgradeName() {
		return "Reduced Pearl Cooldown";
	}

	@Override
	public String getDescription() {
		return "Reduce the Pearl Cooldown while in your claim";
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.ENDER_PEARL);
	}

	@Override
	public int getPrice(int tier) {
		switch (tier) {
			case 1:
				return 35;
			case 2:
				return 55;
			case 3:
				return 90;
			default:
				throw new RuntimeException("This shouldn't happen");
		}
	}

	@Override
	public int getTierLimit() {
		return 3;
	}

}
