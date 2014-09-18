package net.frozenorb.foxtrot.team.claims;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Location;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;

public class LandBoard {
	private static LandBoard instance;

	public static LandBoard getInstance() {
		if (instance == null) {
			instance = new LandBoard();
		}
		return instance;
	}

	private HashMap<Claim, Team> boardMap = new HashMap<Claim, Team>();
	private Map<Claim, Team> backedSyncMap = Collections.synchronizedMap(boardMap);

	public void loadFromTeams() {

		for (Team team : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {
			for (Claim cc : team.getClaims()) {
				System.out.println(cc.getFriendlyName() + "");
				System.out.println(team.getName());
				boardMap.put(cc, team);
			}

		}
		updateSync(null);

		System.out.println("LandBoard has been successfully loaded!");
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
		return backedSyncMap.keySet();
	}

	public synchronized Map<Claim, Team> getBackedSyncMap() {
		return backedSyncMap;
	}
}
