package net.frozenorb.foxtrot.challenges.impl.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class KitBasedChallengeData {

	private final String kitName;
	private final ItemStack[] icons;
	private final int[] counts;
	private final String[] aggressionNames;

}
