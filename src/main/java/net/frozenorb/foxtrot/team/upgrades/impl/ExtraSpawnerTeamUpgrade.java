package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ExtraSpawnerTeamUpgrade implements TeamUpgrade, Listener {

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

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

			if (team != null) {
				for (Claim claim : team.getClaims()) {
					if (claim.contains(event.getBlockPlaced().getLocation())) {
						int spawnerLimit = 5 + getTier(team);

						if (team.getSpawnersInClaim() >= spawnerLimit) {
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.RED + "You can't place more than " + spawnerLimit + " spawners in your claim.");
							event.getPlayer().sendMessage(ChatColor.RED + "To bypass this limit, you can purchase team upgrades via /t upgrades.");
						}

						break;
					}
				}
			}
		}
	}

}
