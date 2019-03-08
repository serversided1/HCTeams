package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.event.CrowbarSpawnerBreakEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerTrackerListener implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

			if (team != null) {
				if (team.getSpawnersInClaim() >= 5 + team.getExtraSpawners()) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "You can't place more than " + (5 + team.getExtraSpawners()) + " spawners in your claim.");
					event.getPlayer().sendMessage(ChatColor.RED + "To bypass this limit, you can purchase team upgrades via /t upgrades.");
					return;
				}

				Claim claim = LandBoard.getInstance().getClaim(event.getBlockPlaced().getLocation());

				if (claim != null && team.getClaims().contains(claim)) {
					team.addSpawnersInClaim(1);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.MOB_SPAWNER) {
			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

			if (team != null) {
				Claim claim = LandBoard.getInstance().getClaim(event.getBlock().getLocation());

				if (claim != null && team.getClaims().contains(claim)) {
					team.removeSpawnersInClaim(1);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawnerBreakEvent(CrowbarSpawnerBreakEvent event) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

		if (team != null) {
			Claim claim = LandBoard.getInstance().getClaim(event.getBlock().getLocation());

			if (claim != null && team.getClaims().contains(claim)) {
				team.removeSpawnersInClaim(1);
			}
		}
	}

}
