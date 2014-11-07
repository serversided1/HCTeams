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
		return instance;
	}

	private HashMap<Claim, Team> boardMap = new HashMap<Claim, Team>();

	public void loadFromTeams() {
		for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
			for (Claim cc : team.getClaims()) {
				boardMap.put(cc, team);
			}
		}

		updateSync(null);
	}

	public Claim getClaimAt(Location loc) {
		for (Claim c : boardMap.keySet()) {
			if (c.contains(loc)) {
				return c;
			}
		}
		return null;
	}

	public Team getTeamAt(Location loc) {
		return boardMap.get(getClaimAt(loc));
	}

	public Team getTeamAt(Claim c) {
		return boardMap.get(c);
	}

	public void setTeamAt(Claim c, Team team) {
		if (team == null) {
			boardMap.remove(c);
			updateSync(c);
			return;
		}
		boardMap.put(c, team);
		updateSync(c);
	}

	public void updateSync(Claim modified) {
		ArrayList<VisualClaim> vcs = new ArrayList<VisualClaim>();
		vcs.addAll(VisualClaim.getCurrentMaps().values());

		for (VisualClaim vc : vcs) {
			if (modified.isWithin(vc.getP().getLocation().getBlockX(), vc.getP().getLocation().getBlockZ(), VisualClaim.MAP_RADIUS)) {
				vc.draw(true);
				vc.draw(true);
			}
		}
	}

	public void clear(Team t) {
		Iterator<Entry<Claim, Team>> iter = boardMap.entrySet().iterator();

		while (iter.hasNext()) {
			Entry<Claim, Team> nxt = iter.next();
			if (nxt.getValue() == t) {
				iter.remove();
			}
		}
	}

	public synchronized Set<Claim> getClaims() {
		return boardMap.keySet();
	}

}
