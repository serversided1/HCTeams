package net.frozenorb.foxtrot.jedis;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;
import redis.clients.jedis.Jedis;

public class RedisSaveTask {

	public static void save() {
		System.out.println("Saving teams to Jedis...");

		JedisCommand<Integer> jdc = new JedisCommand<Integer>() {

			@Override
			public Integer execute(Jedis jedis) {
                int changed = 0;

				for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
					if (team.isChanged()) {
                        changed++;
                        jedis.set("fox_teams." + team.getName().toLowerCase(), team.saveString());
					}
				}

                jedis.set("TeamsLastUpdated", String.valueOf((float) (System.currentTimeMillis() / 1000L)));
				return (changed);
			}
		};

		int teamsSaved = FoxtrotPlugin.getInstance().runJedisCommand(jdc);
        System.out.println("Saved " + teamsSaved + " teams to Jedis.");
    }

}