package net.frozenorb.foxtrot.team;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;

import org.bukkit.Location;
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

	public boolean isTaken(Claim cc) {
		return getOwner(cc) != null;
	}

	public boolean isTaken(Location loc) {
		return getOwner(loc) != null;
	}

	public Team getOwner(Claim cc) {
		return LandBoard.getInstance().getTeamAt(cc);
	}

	public Team getOwner(Location loc) {
		return LandBoard.getInstance().getTeamAt(loc);
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

	public void renameTeam(Team team, String name) {
		if (teamExists(name)) {
			return;
		}
		final String oldName = team.getName();

		team.setName(name.toLowerCase());
		team.setFriendlyName(name);

        for (String member : team.getMembers()) {
            setTeam(member, team);
        }

		addTeam(team);

		teamNameMap.remove(oldName.toLowerCase());

        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {
            public Object execute(Jedis jedis) {
                jedis.del("fox_teams." + oldName.toLowerCase());
                return null;
            }
        });
	}

	public void removeTeam(final String name) {
		if (teamExists(name)) {
			Team t = getTeam(name);
			for (String names : t.getMembers()) {
				removePlayerFromTeam(names);
			}

			LandBoard.getInstance().clear(t);
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