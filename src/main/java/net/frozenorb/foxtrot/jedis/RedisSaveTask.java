package net.frozenorb.foxtrot.jedis;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.imagemessage.ImageMessage;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RedisSaveTask extends BukkitRunnable {

    public void run() {
        save(false);
    }

    public static int save(boolean forceAll) {
        System.out.println("Saving teams to Jedis...");

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
        Map<String, String> dealtWith = new HashMap<String, String>();
        Set<String> errors = new HashSet<String>();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            for (String member : team.getMembers()) {
                if (dealtWith.containsKey(member) && !errors.contains(member)) {
                    errors.add(member);
                    continue;
                }

                dealtWith.put(member, team.getName());
            }
        }

        new ImageMessage("redis-saved").appendText(
                "",
                "",
                ChatColor.DARK_PURPLE + "Saved all teams to Redis.",
                ChatColor.DARK_AQUA + "Teams: " + ChatColor.WHITE + teamsSaved,
                ChatColor.DARK_AQUA + "Elapsed: " + ChatColor.WHITE + time + "ms",
                ChatColor.DARK_AQUA + "Errors: " + ChatColor.WHITE + errors
        ).sendOPs();

        return (teamsSaved);
    }

}