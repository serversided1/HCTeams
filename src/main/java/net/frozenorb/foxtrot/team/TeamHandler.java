package net.frozenorb.foxtrot.team;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.CommandHandler;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.team.dtr.bitmask.tabcompleter.DTRBitmaskTypeTabCompleter;
import net.frozenorb.foxtrot.team.dtr.bitmask.transformer.DTRBitmaskTypeTransformer;
import net.frozenorb.foxtrot.team.subclaim.tabcompleter.SubclaimTabCompleter;
import net.frozenorb.foxtrot.team.subclaim.transformer.SubclaimTransformer;
import net.frozenorb.foxtrot.team.tabcompleter.TeamTabCompleter;
import net.frozenorb.foxtrot.team.transformer.TeamTransformer;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TeamHandler {

    @Getter private volatile ConcurrentHashMap<ObjectId, Team> teamUniqueIdMap = new ConcurrentHashMap<ObjectId, Team>();
    @Getter private volatile ConcurrentHashMap<String, Team> teamNameMap = new ConcurrentHashMap<String, Team>();
    @Getter private volatile ConcurrentHashMap<String, Team> playerTeamMap = new ConcurrentHashMap<String, Team>();

    public TeamHandler() {
        CommandHandler.registerTransformer(Team.class, new TeamTransformer());
        CommandHandler.registerTabCompleter(Team.class, new TeamTabCompleter());
        CommandHandler.registerTransformer(DTRBitmaskType.class, new DTRBitmaskTypeTransformer());
        CommandHandler.registerTabCompleter(DTRBitmaskType.class, new DTRBitmaskTypeTabCompleter());
        CommandHandler.registerTransformer(Subclaim.class, new SubclaimTransformer());
        CommandHandler.registerTabCompleter(Subclaim.class, new SubclaimTabCompleter());

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

    public Team getTeam(ObjectId teamUniqueId) {
        if (teamUniqueId == null) {
            return (null);
        }

        return (teamUniqueIdMap.get(teamUniqueId));
    }

    private void loadTeams() {
        FoxtrotPlugin.getInstance().runJedisCommand(new JedisCommand<Object>() {

            @Override
            public Object execute(Jedis jedis) {
                for (String key : jedis.keys("fox_teams.*")) {
                    String loadString = jedis.get(key);

                    Team team = new Team(key.split("\\.")[1]);
                    team.load(loadString);

                    teamNameMap.put(team.getName().toLowerCase(), team);
                    teamUniqueIdMap.put(team.getUniqueId(), team);

                    for (String member : team.getMembers()) {
                        playerTeamMap.put(member.toLowerCase(), team);
                    }
                }

                return (null);
            }

        });
    }

    public boolean isTaken(Location location) {
        return (getOwner(location) != null);
    }

    public Team getOwner(Claim claim) {
        return (LandBoard.getInstance().getTeamAt(claim));
    }

    public Team getOwner(Location location) {
        return (LandBoard.getInstance().getTeamAt(location));
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
        teamUniqueIdMap.put(team.getUniqueId(), team);

        for (String member : team.getMembers()) {
            playerTeamMap.put(member.toLowerCase(), team);
        }
    }

}