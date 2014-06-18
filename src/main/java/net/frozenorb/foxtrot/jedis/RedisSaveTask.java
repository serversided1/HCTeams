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

		JedisCommand<Object> jdc = new JedisCommand<Object>() {

			@Override
			public Object execute(Jedis jedis) {
				for (Team t : FoxtrotPlugin.getInstance().getTeamManager().getTeams()) {
					if (t.hasChanged()) {
						t.save();
					}
				}

				FoxtrotPlugin.getInstance().getDeathbanMap().saveToRedis();
				FoxtrotPlugin.getInstance().getOppleMap().saveToRedis();
				FoxtrotPlugin.getInstance().getPlaytimeMap().saveToRedis();
				FoxtrotPlugin.getInstance().getJoinTimerMap().saveToRedis();

				return null;
			}
		};

		FoxtrotPlugin.getInstance().runJedisCommand(jdc);

	}

}
