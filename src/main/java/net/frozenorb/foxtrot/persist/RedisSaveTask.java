package net.frozenorb.foxtrot.persist;

import com.mongodb.DBCollection;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

public class RedisSaveTask extends BukkitRunnable {

    public void run() {
        save(false);
    }

    public static int save(final boolean forceAll) {
        System.out.println("Saving teams to Redis...");
        FoxtrotPlugin.getInstance().sendOPMessage(ChatColor.DARK_PURPLE + "Saving teams to Redis...");

        JedisCommand<Integer> jdc = new JedisCommand<Integer>() {

            @Override
            public Integer execute(Jedis jedis) {
                DBCollection teamsCollection = FoxtrotPlugin.getInstance().getMongoPool().getDB("HCTeams").getCollection("Teams");
                int changed = 0;

                for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
                    if (team.isNeedsSave() || forceAll) {
                        changed++;

                        jedis.set("fox_teams." + team.getName().toLowerCase(), team.saveString(true));
                        teamsCollection.update(team.getJSONIdentifier(), team.toJSON(), true, false);
                    }
                }

                return (changed);
            }
        };

        long startMs = System.currentTimeMillis();
        int teamsSaved = FoxtrotPlugin.getInstance().runJedisCommand(jdc);
        int time = (int) (System.currentTimeMillis() - startMs);

        System.out.println("Saved " + teamsSaved + " teams to Redis in " + time + "ms.");
        FoxtrotPlugin.getInstance().sendOPMessage(ChatColor.DARK_PURPLE + "Saved " + teamsSaved + " teams to Redis in " + time + "ms.");

        return (teamsSaved);
    }

}