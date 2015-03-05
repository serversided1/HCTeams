package net.frozenorb.foxtrot.team;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.persist.JedisCommand;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRBitmaskType;
import net.frozenorb.foxtrot.team.subclaim.SubclaimType;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeamHandler {

    private Map<String, Team> teamNameMap = new ConcurrentHashMap<>(); // Team Name -> Team
    private Map<ObjectId, Team> teamUniqueIdMap = new ConcurrentHashMap<>(); // Team UUID -> Team
    private Map<UUID, Team> playerTeamMap = new ConcurrentHashMap<>(); // Player UUID -> Team

    public TeamHandler() {
        FrozenCommandHandler.registerParameterType(Team.class, new TeamType());
        FrozenCommandHandler.registerParameterType(DTRBitmask.class, new DTRBitmaskType());
        FrozenCommandHandler.registerParameterType(Subclaim.class, new SubclaimType());

        // Load teams from Redis.
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

    public List<Team> getTeams() {
        return (new ArrayList<>(teamNameMap.values()));
    }

    public Team getTeam(String teamName) {
        return (teamNameMap.get(teamName.toLowerCase()));
    }

    public Team getTeam(ObjectId teamUUID) {
        return (teamUUID == null ? null : teamUniqueIdMap.get(teamUUID));
    }

    public Team getTeam(UUID playerUUID) {
        return (playerUUID == null ? null : playerTeamMap.get(playerUUID));
    }

    public Team getTeam(Player player) {
        return (getTeam(player.getUniqueId()));
    }

    public void setTeam(UUID playerUUID, Team team) {
        if (team == null) {
             playerTeamMap.remove(playerUUID);
        } else {
            playerTeamMap.put(playerUUID, team);
        }
    }

    public void setupTeam(Team team) {
        teamNameMap.put(team.getName().toLowerCase(), team);
        teamUniqueIdMap.put(team.getUniqueId(), team);

        for (UUID member : team.getMembers()) {
            setTeam(member, team);
        }
    }

    public void removeTeam(Team team) {
        teamNameMap.remove(team.getName().toLowerCase());
        teamUniqueIdMap.remove(team.getUniqueId());

        for (UUID member : team.getMembers()) {
            setTeam(member, null);
        }
    }

    public void recachePlayerTeams() {
        playerTeamMap.clear();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            for (UUID member : team.getMembers()) {
                setTeam(member, team);
            }
        }
    }

}