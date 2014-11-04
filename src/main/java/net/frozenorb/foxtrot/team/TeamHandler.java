package net.frozenorb.foxtrot.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.CommandHandler;
import net.frozenorb.foxtrot.command.objects.ParamTabCompleter;
import net.frozenorb.foxtrot.command.objects.ParamTransformer;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.mBasic.Basic;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TeamHandler {

	private volatile ConcurrentHashMap<String, Team> teamNameMap = new ConcurrentHashMap<String, Team>();
	private volatile ConcurrentHashMap<String, Team> playerTeamMap = new ConcurrentHashMap<String, Team>();

	public TeamHandler() {
        CommandHandler.registerTransformer(Team.class, new ParamTransformer() {

            @Override
            public Object transform(Player sender, String source) {
                if (source.equalsIgnoreCase("self") || source.equals("")) {
                    return (getPlayerTeam(sender.getName()));
                }

                Team team = getTeam(source);

                if (team == null) {
                    sender.sendMessage(ChatColor.RED + "No team with the name " + source + " found.");
                    return (null);
                }

                return (team);
            }

        });

        CommandHandler.registerTabCompleter(Team.class, new ParamTabCompleter() {

            public List<String> tabComplete(Player sender, String source) {
                List<String> completions = new ArrayList<String>();

                for (Team team : getTeams()) {
                    if (StringUtils.startsWithIgnoreCase(team.getFriendlyName(), source)) {
                        completions.add(team.getFriendlyName());
                    }
                }

                return (completions);
            }

        });

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

            //Refund owner
            Basic.get().getEconomyManager().depositPlayer(t.getOwner(), t.getBalance());

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