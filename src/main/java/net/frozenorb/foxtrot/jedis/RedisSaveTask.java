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
				for (Team t : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
					if (t.isChanged()) {
						t.save(jedis);
					}
				}

				FoxtrotPlugin.getInstance().getDeathbanMap().executeSave(jedis);
				FoxtrotPlugin.getInstance().getOppleMap().executeSave(jedis);
				FoxtrotPlugin.getInstance().getPlaytimeMap().executeSave(jedis);
				FoxtrotPlugin.getInstance().getJoinTimerMap().executeSave(jedis);

				return null;
			}
		};

		System.out.println("Redis save finished!");

		FoxtrotPlugin.getInstance().runJedisCommand(jdc);

	}

}
