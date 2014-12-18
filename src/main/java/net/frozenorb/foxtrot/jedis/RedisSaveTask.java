package net.frozenorb.foxtrot.jedis;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

public class RedisSaveTask extends BukkitRunnable {

    public void run() {
        save(false);
    }

    public static int save(boolean forceAll) {
        System.out.println("Saving teams to Redis...");
        FoxtrotPlugin.getInstance().sendOPMessage(ChatColor.DARK_PURPLE + "Saving teams to Redis...");

        JedisCommand<Integer> jdc = new JedisCommand<Integer>() {

            @Override
            public Integer execute(Jedis jedis) {
                int changed = 0;

                for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
                    if (team.isNeedsSave() || forceAll) {
                        changed++;
                        jedis.set("fox_teams." + team.getName().toLowerCase(), team.saveString(true));
                    }
                }

                jedis.set("TeamsLastUpdated", String.valueOf((float) (System.currentTimeMillis() / 1000L)));
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