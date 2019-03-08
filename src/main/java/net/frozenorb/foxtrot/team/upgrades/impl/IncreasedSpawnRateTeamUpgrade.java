package net.frozenorb.foxtrot.team.upgrades.impl;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class IncreasedSpawnRateTeamUpgrade implements TeamUpgrade, Listener {

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

	@Override
	public void onPurchase(Player player, Team team, int tier, int price) {
		Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
			for (CreatureSpawner creatureSpawner : team.findSpawners()) {
				creatureSpawner.setDelay(creatureSpawner.getDelay() / tier);
			}
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

			if (team != null) {
				for (Claim claim : team.getClaims()) {
					if (claim.contains(event.getBlockPlaced().getLocation())) {
						if (getTier(team) > 0) {
							CreatureSpawner creatureSpawner = (CreatureSpawner) event.getBlockPlaced().getState();
							creatureSpawner.setDelay(creatureSpawner.getDelay() / getTier(team));
						}

						break;
					}
				}
			}
		}
	}

}
