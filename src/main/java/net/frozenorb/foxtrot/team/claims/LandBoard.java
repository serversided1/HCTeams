package net.frozenorb.foxtrot.team.claims;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;

import java.util.*;
import java.util.Map.Entry;

public class LandBoard {

	private static LandBoard instance;

	public static LandBoard getInstance() {
		if (instance == null) {
			instance = new LandBoard();
		}

		return (instance);
	}

	private Map<Claim, Team> boardMap = new HashMap<Claim, Team>();

	public void loadFromTeams() {
		for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
			for (Claim cc : team.getClaims()) {
				boardMap.put(cc, team);
			}
		}

		updateSync(null);
	}

	public Claim getClaimAt(Location location) {
		for (Claim claim : boardMap.keySet()) {
			if (claim.contains(location)) {
				return (claim);
			}
		}

		return (null);
	}

	public Team getTeamAt(Location location) {
		return (boardMap.get(getClaimAt(location)));
	}

	public Team getTeamAt(Claim claim) {
		return (boardMap.get(claim));
	}

	public void setTeamAt(Claim claim, Team team) {
		if (team == null) {
			boardMap.remove(claim);
			updateSync(claim);
			return;
		}

		boardMap.put(claim, team);
		updateSync(claim);
	}

	public void updateSync(Claim modified) {
		ArrayList<VisualClaim> vcs = new ArrayList<VisualClaim>();
		vcs.addAll(VisualClaim.getCurrentMaps().values());

		for (VisualClaim vc : vcs) {
			if (modified.isWithin(vc.getPlayer().getLocation().getBlockX(), vc.getPlayer().getLocation().getBlockZ(), VisualClaim.MAP_RADIUS, modified.getWorld())) {
				vc.draw(true);
				vc.draw(true);
			}
		}
	}

	public void clear(Team teams) {
		Iterator<Entry<Claim, Team>> iter = boardMap.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<Claim, Team> nxt = iter.next();

			if (nxt.getValue() == teams) {
				iter.remove();
			}
		}
	}

	public synchronized Set<Claim> getClaims() {
		return (boardMap.keySet());
	}

}