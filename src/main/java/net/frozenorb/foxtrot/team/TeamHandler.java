package net.frozenorb.foxtrot.team;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.CommandHandler;
import net.frozenorb.foxtrot.command.objects.ParamTabCompleter;
import net.frozenorb.foxtrot.command.objects.ParamTransformer;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.team.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TeamHandler {

	@Getter private volatile ConcurrentHashMap<String, Team> teamNameMap = new ConcurrentHashMap<String, Team>();
	@Getter private volatile ConcurrentHashMap<String, Team> playerTeamMap = new ConcurrentHashMap<String, Team>();

	public TeamHandler() {
        CommandHandler.registerTransformer(Team.class, new ParamTransformer() {

            @Override
            public Object transform(Player sender, String source) {
                if (source.equalsIgnoreCase("self") || source.equals("")) {
                    Team team = getPlayerTeam(sender.getName());

                    if (team == null) {
                        sender.sendMessage(ChatColor.GRAY + "You're not on a team!");
                    }

                    return (team);
                }

                Team team = getTeam(source);

                if (team == null) {
                    Player bukkitPlayer = FoxtrotPlugin.getInstance().getServer().getPlayer(source);

                    if (bukkitPlayer != null) {
                        source = bukkitPlayer.getName();
                    }

                    team =  getPlayerTeam(source);

                    if (team == null) {
                        sender.sendMessage(ChatColor.RED + "No team with the name or member " + source + " found.");
                        return (null);
                    }
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

                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
                        completions.add(player.getName());
                    }
                }

                return (completions);
            }

        });

        CommandHandler.registerTransformer(DTRBitmaskType.class, new ParamTransformer() {

            @Override
            public Object transform(Player sender, String source) {
                for (DTRBitmaskType bitmaskType : DTRBitmaskType.values()) {
                    if (bitmaskType.getName().equalsIgnoreCase(bitmaskType.getName())) {
                        return (bitmaskType);
                    }
                }

                sender.sendMessage(ChatColor.RED + "No bitmask type with the name " + source + " found.");
                return (null);
            }

        });

        CommandHandler.registerTabCompleter(DTRBitmaskType.class, new ParamTabCompleter() {

            @Override
            public List<String> tabComplete(Player sender, String source) {
                List<String> completions = new ArrayList<String>();
                Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

                if (team == null) {
                    return (completions);
                }

                for (Subclaim subclaim : team.getSubclaims()) {
                    if (StringUtils.startsWithIgnoreCase(subclaim.getName(), source)) {
                        completions.add(subclaim.getName());
                    }
                }

                return (completions);
            }

        });

        CommandHandler.registerTransformer(Subclaim.class, new ParamTransformer() {

            @Override
            public Object transform(Player sender, String source) {
                Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(sender.getName());

                if (team == null) {
                    sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
                    return (null);
                }

                Subclaim subclaim = team.getSubclaim(source);

                if (subclaim == null) {
                    sender.sendMessage(ChatColor.RED + "No subclaim with the name " + source + " found.");
                    return (null);
                }

                return (subclaim);
            }

        });

        CommandHandler.registerTabCompleter(Subclaim.class, new ParamTabCompleter() {

            @Override
            public List<String> tabComplete(Player sender, String source) {
                List<String> completions = new ArrayList<String>();

                for (DTRBitmaskType bitmaskType : DTRBitmaskType.values()) {
                    if (StringUtils.startsWithIgnoreCase(bitmaskType.getName(), source)) {
                        completions.add(bitmaskType.getName());
                    }
                }

                return (completions);
            }

        });

		loadTeams();
	}

	public List<Team> getTeams() {
		return (new ArrayList<Team>(teamNameMap.values()));
	}

	public void setTeam(String playerName, Team team) {
		playerTeamMap.put(playerName.toLowerCase(), team);
	}

	public Team getTeam(String teamName) {
		return (teamNameMap.get(teamName.toLowerCase()));
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

				return (null);
			}

		});
	}

	public boolean isTaken(Location loc) {
		return (getOwner(loc) != null);
	}

	public Team getOwner(Claim cc) {
		return (LandBoard.getInstance().getTeamAt(cc));
	}

	public Team getOwner(Location loc) {
		return (LandBoard.getInstance().getTeamAt(loc));
	}

	public Team getPlayerTeam(String name) {
		if (!playerTeamMap.containsKey(name.toLowerCase())) {
            return (null);
        }

		return (playerTeamMap.get(name.toLowerCase()));
	}

	public void addTeam(Team team) {
		team.flagForSave();
		teamNameMap.put(team.getName().toLowerCase(), team);

		for (String member : team.getMembers()) {
			playerTeamMap.put(member, team);
		}
	}

}