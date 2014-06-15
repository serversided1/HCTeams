package net.frozenorb.foxtrot.team;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.JedisCommand;

import org.bukkit.plugin.java.JavaPlugin;

import redis.clients.jedis.Jedis;

public class TeamManager {
	private volatile ConcurrentHashMap<String, Team> teamNameMap = new ConcurrentHashMap<String, Team>();
	private volatile ConcurrentHashMap<String, Team> playerTeamMap = new ConcurrentHashMap<String, Team>();

	public TeamManager(JavaPlugin plugin) {
		loadTeams();
	}

	public ArrayList<Team> getTeams() {
		return new ArrayList<>(teamNameMap.values());
	}

	public void setTeam(String playerName, Team team) {
		playerTeamMap.put(playerName.toLowerCase(), team);
	}

	public Team getTeam(String teamName) {
		return teamNameMap.get(teamName.toLowerCase());
	}

	private void loadTeams() {
		FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {
			@Override
			public Object execute(Jedis jedis) {
				for (String key : jedis.keys("fox_teams.*")) {
					String str = jedis.get(key);
					Team team = new Team(key.split("\\.")[1]);
					team.load(str);
					teamNameMap.put(team.getName().toLowerCase(), team);
					for (String member : team.getMembers()) {
						playerTeamMap.put(member.toLowerCase(), team);
					}
				}
				return null;
			}
		});

	}

	public boolean isTaken(ClaimedChunk cc) {
		for (Team t : teamNameMap.values()) {
			if (t.getChunks().contains(cc)) {
				return true;
			}
		}
		return false;
	}

	public Team getOwner(ClaimedChunk cc) {
		for (Team t : teamNameMap.values()) {
			if (t.getChunks().contains(cc)) {
				return t;
			}
		}
		return null;
	}

	public Team getPlayerTeam(String name) {
		if (!playerTeamMap.containsKey(name.toLowerCase()))
			return null;

		return playerTeamMap.get(name.toLowerCase());
	}

	public boolean teamExists(String teamName) {
		return teamNameMap.containsKey(teamName.toLowerCase());
	}

	public void addTeam(Team team) {
		team.setChanged(true);
		teamNameMap.put(team.getName().toLowerCase(), team);

		for (String member : team.getMembers()) {
			playerTeamMap.put(member, team);
		}
	}

	public void removePlayerFromTeam(String name) {
		playerTeamMap.remove(name.toLowerCase());
	}

	public boolean isOnTeam(String name) {
		return playerTeamMap.containsKey(name.toLowerCase());
	}

	public void removeTeam(final String name) {
		if (teamExists(name)) {
			Team t = getTeam(name);
			for (String names : t.getMembers()) {
				removePlayerFromTeam(names);
			}
		}
		teamNameMap.remove(name.toLowerCase());

		FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {
			@Override
			public Object execute(Jedis jedis) {
				jedis.del("fox_teams." + name.toLowerCase());
				return null;
			}
		});
	}
}