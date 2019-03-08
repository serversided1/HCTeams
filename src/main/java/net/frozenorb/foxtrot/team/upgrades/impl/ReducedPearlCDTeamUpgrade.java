package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ReducedPearlCDTeamUpgrade implements TeamUpgrade, Listener {

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

	@EventHandler
	public void onEnderpearlCooldownAppliedEvent(EnderpearlCooldownAppliedEvent event) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

		if (team != null) {
			for (Claim claim : team.getClaims()) {
				if (claim.contains(event.getPlayer())) {
					if (getTier(team) > 0) {
						long timeToApply = event.getTimeToApply();

						switch (getTier(team)) {
							case 1:
								timeToApply = 13_000L;
								break;
							case 2:
								timeToApply = 8_000L;
								break;
							case 3:
								timeToApply = 4_000L;
								break;
						}

						event.setTimeToApply(timeToApply);
					}

					break;
				}
			}
		}
	}

}
