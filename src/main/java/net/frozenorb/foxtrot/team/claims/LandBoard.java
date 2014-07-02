package net.frozenorb.foxtrot.team.claims;

import java.util.HashMap;
import java.util.Iterator;
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

	private HashMap<PhysicalChunk, Team> boardMap = new HashMap<PhysicalChunk, Team>();

	public void loadFromTeams() {

		for (Team team : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {
			for (PhysicalChunk cc : team.getChunks()) {
				boardMap.put(cc, team);
			}

		}
		System.out.println("LandBoard has been successfully loaded!");
	}

	public Team getTeamAt(Location loc) {
		return boardMap.get(new PhysicalChunk(loc));
	}

	public Team getTeamAt(PhysicalChunk cc) {
		return boardMap.get(cc);
	}

	public void setTeamAt(Location loc, Team team) {
		if (team == null) {
			boardMap.remove(new PhysicalChunk(loc));
		}
		boardMap.put(new PhysicalChunk(loc), team);
	}

	public void setTeamAt(PhysicalChunk cc, Team team) {
		if (team == null) {
			boardMap.remove(cc);
		}
		boardMap.put(cc, team);
	}

	public void clear(Team t) {
		Iterator<Entry<PhysicalChunk, Team>> iter = boardMap.entrySet().iterator();

		while (iter.hasNext()) {
			if (iter.next().getValue() == t) {
				iter.remove();
			}
		}
	}
}
