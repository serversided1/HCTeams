package net.frozenorb.foxtrot.jedis;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.Team;

import org.bukkit.scheduler.BukkitRunnable;

import redis.clients.jedis.Jedis;

public class RedisSaveTask extends BukkitRunnable {

	@Getter private static RedisSaveTask instance;

	public RedisSaveTask() {
		instance = this;
	}

	@Override
	public void run() {
		save();
	}

	public void save() {
		System.out.println("Saving teams to Jedis...");

		JedisCommand<Integer> jdc = new JedisCommand<Integer>() {

			@Override
			public Integer execute(Jedis jedis) {
                int changed = 0;

				for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
					if (team.isChanged()) {
                        changed++;
						team.save(jedis);
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