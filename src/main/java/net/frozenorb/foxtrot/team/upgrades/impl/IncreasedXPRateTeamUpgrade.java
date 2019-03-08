package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

public class IncreasedXPRateTeamUpgrade implements TeamUpgrade, Listener {

	@Override
	public String getUpgradeName() {
		return "Increased XP Rate";
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

	@EventHandler
	public void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

		if (team != null) {
			for (Claim claim : team.getClaims()) {
				if (claim.contains(event.getPlayer())) {
					int xpToApply = event.getAmount();

					if (getTier(team) > 0) {
						switch (getTier(team)) {
							case 1:
								xpToApply *= 2;
								break;
							case 2:
								xpToApply *= 3;
								break;
							case 3:
								xpToApply *= 4;
								break;
						}
					}

					event.setAmount(xpToApply);
					break;
				}
			}
		}
	}

}
