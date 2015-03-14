package net.frozenorb.foxtrot.persist;

import com.mongodb.DBCollection;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.redis.RedisCommand;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

public class RedisSaveTask extends BukkitRunnable {

    public void run() {
        save(false);
    }

    public static int save(final boolean forceAll) {
        long startMs = System.currentTimeMillis();
        int teamsSaved = qLib.getInstance().runRedisCommand(new RedisCommand<Integer>() {

            @Override
            public Integer execute(Jedis redis) {
                DBCollection teamsCollection = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Teams");
                int changed = 0;

                for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
                    if (team.isNeedsSave() || forceAll) {
                        changed++;

                        redis.set("fox_teams." + team.getName().toLowerCase(), team.saveString(true));
                        teamsCollection.update(team.getJSONIdentifier(), team.toJSON(), true, false);
                    }
                }

                return (changed);
            }
        });

        int time = (int) (System.currentTimeMillis() - startMs);

        System.out.println("Saved " + teamsSaved + " teams to Redis in " + time + "ms.");
        FoxtrotPlugin.getInstance().sendOPMessage(ChatColor.DARK_PURPLE + "Saved " + teamsSaved + " teams to Redis in " + time + "ms.");

        return (teamsSaved);
    }

}