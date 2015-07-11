package net.frozenorb.foxtrot.team;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRBitmaskType;
import net.frozenorb.foxtrot.team.subclaim.SubclaimType;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.redis.RedisCommand;
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
    @Getter @Setter private boolean rostersLocked = false;

    public TeamHandler() {
        FrozenCommandHandler.registerParameterType(Team.class, new TeamType());
        FrozenCommandHandler.registerParameterType(DTRBitmask.class, new DTRBitmaskType());
        FrozenCommandHandler.registerParameterType(Subclaim.class, new SubclaimType());

        // Load teams from Redis.
        qLib.getInstance().runRedisCommand(new RedisCommand<Object>() {

            @Override
            public Object execute(Jedis redis) {
                for (String key : redis.keys("fox_teams.*")) {
                    String loadString = redis.get(key);

                    try {
                        Team team = new Team(key.split("\\.")[1]);
                        team.load(loadString);

                        setupTeam(team);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Foxtrot.getInstance().getLogger().severe("Could not load team from raw string: " + loadString);
                    }
                }

                rostersLocked = Boolean.valueOf(redis.get("RostersLocked"));
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

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            for (UUID member : team.getMembers()) {
                setTeam(member, team);
            }
        }
    }

}