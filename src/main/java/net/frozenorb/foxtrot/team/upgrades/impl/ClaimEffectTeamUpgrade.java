package net.frozenorb.foxtrot.team.upgrades.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

@AllArgsConstructor
public class ClaimEffectTeamUpgrade implements TeamUpgrade {

	private String upgradeName;
	private int basePrice;
	private int priceIncrement;
	private int tierLimit;
	@Getter private ItemStack icon;
	@Getter private PotionEffectType potionEffectType;

	@Override
	public String getUpgradeName() {
		return upgradeName;
	}

	@Override
	public String getDescription() {
		return "Receive " + upgradeName + " while in your claim";
	}

	@Override
	public int getTierLimit() {
		return tierLimit;
	}

	@Override
	public int getPrice(int tier) {
		return basePrice + ((tier - 1) * priceIncrement);
	}

}
