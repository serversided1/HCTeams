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
		System.out.println("Redis save initiated!");
		JedisCommand<Object> jdc = new JedisCommand<Object>() {

			@Override
			public Object execute(Jedis jedis) {
				for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
					if (team.isChanged()) {
						team.save(jedis);
					}
				}

				FoxtrotPlugin.getInstance().getDeathbanMap().executeSave(jedis);
				FoxtrotPlugin.getInstance().getOppleMap().executeSave(jedis);
				FoxtrotPlugin.getInstance().getPlaytimeMap().executeSave(jedis);
				FoxtrotPlugin.getInstance().getJoinTimerMap().executeSave(jedis);

                // I doubt this way of converting the time to a float works as it should...
                jedis.set("last_updated", String.valueOf((float) (System.currentTimeMillis() / 1000L)));

				return null;
			}
		};

		FoxtrotPlugin.getInstance().runJedisCommand(jdc);
        System.out.println("Redis save finished!");
    }

}
