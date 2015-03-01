package net.frozenorb.foxtrot.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRBitmaskType;
import net.frozenorb.foxtrot.team.subclaim.SubclaimType;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import org.bson.types.ObjectId;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeamHandler {

    private volatile Map<ObjectId, Team> teamUniqueIdMap = new ConcurrentHashMap<>();
    private volatile Map<String, Team> teamNameMap = new ConcurrentHashMap<>();
    private volatile Map<String, Team> playerTeamMap = new ConcurrentHashMap<>();

    public TeamHandler() {
        FrozenCommandHandler.registerParameterType(Team.class, new TeamType());
        FrozenCommandHandler.registerParameterType(DTRBitmask.class, new DTRBitmaskType());
        FrozenCommandHandler.registerParameterType(Subclaim.class, new SubclaimType());

        loadTeams();
    }

    public List<Team> getTeams() {
        return (new ArrayList<>(teamNameMap.values()));
    }

    public Team getTeam(String teamName) {
        return (teamNameMap.get(teamName.toLowerCase()));
    }

    public Team getTeam(ObjectId teamUniqueId) {
        if (teamUniqueId == null) {
            return (null);
        }

        return (teamUniqueIdMap.get(teamUniqueId));
    }

    public void setTeam(String playerName, Team team) {
        if (team == null) {
             playerTeamMap.remove(playerName.toLowerCase());
        } else {
            playerTeamMap.put(playerName.toLowerCase(), team);
        }
    }

    private void loadTeams() {
        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                for (String key : jedis.keys("fox_teams.*")) {
                    String loadString = jedis.get(key);

                    Team team = new Team(key.split("\\.")[1]);
                    team.load(loadString);

                    setupTeam(team);
                }

                return (null);
            }

        });
    }

    public Team getPlayerTeam(String name) {
        if (!playerTeamMap.containsKey(name.toLowerCase())) {
            return (null);
        }

        return (playerTeamMap.get(name.toLowerCase()));
    }

    public void setupTeam(Team team) {
        teamNameMap.put(team.getName().toLowerCase(), team);
        teamUniqueIdMap.put(team.getUniqueId(), team);

        for (String member : team.getMembers()) {
            setTeam(member, team);
        }
    }

    public void removeTeam(Team team) {
        teamNameMap.remove(team.getName().toLowerCase());
        teamUniqueIdMap.remove(team.getUniqueId());

        for (String member : team.getMembers()) {
            setTeam(member, null);
        }
    }

    public void recachePlayerTeams() {
        playerTeamMap.clear();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            for (String member : team.getMembers()) {
                setTeam(member, team);
            }
        }
    }

}