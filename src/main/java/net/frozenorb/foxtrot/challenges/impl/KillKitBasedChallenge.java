package net.frozenorb.foxtrot.challenges.impl;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KillKitBasedChallenge extends KillBasedChallenge {

	@Getter
	private final String killerKitName;
	@Getter
	private final String victimKitName;

	public KillKitBasedChallenge(String name, ItemStack icon, int count, String killerKitName, String victimKitName) {
		super(name, "Kill " + count + " people in " + victimKitName.toLowerCase() + " while in " + killerKitName.toLowerCase(), icon, count);

		this.killerKitName = killerKitName;
		this.victimKitName = victimKitName;
	}

	@Override
	public boolean counts(Player killer, Player victim) {
		return hasKit(killer, killerKitName) && hasKit(victim, victimKitName);
	}

}
