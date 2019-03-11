package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.event.FullTeamBypassEvent;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ExtraSlotTeamUpgrade implements TeamUpgrade, Listener {

	@Override
	public String getUpgradeName() {
		return "Extra Team Slot";
	}

	@Override
	public String getDescription() {
		return "Increase your maximum team size by 1 slot";
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	}

	@Override
	public int getTierLimit() {
		return 10;
	}

	@Override
	public int getPrice(int tier) {
		return 30 + ((tier - 1) * 10);
	}

	@EventHandler
	public void onFullTeamBypassEvent(FullTeamBypassEvent event) {
		event.setExtraSlots(getTier(event.getTeam()));

		if (getTier(event.getTeam()) > 0) {
			if (event.getTeam().getMembers().size() + 1 <= Foxtrot.getInstance().getMapHandler().getTeamSize() + getTier(event.getTeam())) {
				event.setAllowBypass(true);
			}
		}
	}

}
