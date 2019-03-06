package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.event.SpawnerBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerTrackerListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

		if (team != null) {
			Claim claim = LandBoard.getInstance().getClaim(event.getBlockPlaced().getLocation());

			if (claim != null && team.getClaims().contains(claim)) {
				team.incrementSpawnersInClaim();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

		if (team != null) {
			Claim claim = LandBoard.getInstance().getClaim(event.getBlock().getLocation());

			if (claim != null && team.getClaims().contains(claim)) {
				team.decrementSpawnersInClaim();
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSpawnerBreakEvent(SpawnerBreakEvent event) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

		if (team != null) {
			Claim claim = LandBoard.getInstance().getClaim(event.getBlock().getLocation());

			if (claim != null && team.getClaims().contains(claim)) {
				team.incrementSpawnersInClaim();
			}
		}
	}

}
