package net.frozenorb.foxtrot;

import lombok.Getter;
import net.frozenorb.foxtrot.command.CommandRegistrar;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import net.frozenorb.foxtrot.jedis.persist.OppleMap;
import net.frozenorb.foxtrot.jedis.persist.PlaytimeMap;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.server.ServerManager;
import net.frozenorb.foxtrot.team.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public class FoxtrotPlugin extends JavaPlugin {
	private static FoxtrotPlugin instance;

	/*
	 * ---- FIELDS ----
	 */
	private JedisPool pool;

	@Getter private TeamManager teamManager;
	@Getter private ServerManager serverManager;

	@Getter private PlaytimeMap playtimeMap;
	@Getter OppleMap oppleMap;

	@Override
	public void onEnable() {
		instance = this;
		pool = new JedisPool(new JedisPoolConfig(), "localhost");

		new RedisSaveTask().runTaskTimer(this, 13200L, 13200L);

		new CommandRegistrar().register();

		teamManager = new TeamManager(this);
		serverManager = new ServerManager();

		playtimeMap = new PlaytimeMap();
		playtimeMap.loadFromRedis();

		oppleMap = new OppleMap();
		oppleMap.loadFromRedis();

		Bukkit.getPluginManager().registerEvents(new BorderListener(), this);
		Bukkit.getPluginManager().registerEvents(new FoxListener(), this);

		for (Player p : Bukkit.getOnlinePlayers()) {
			playtimeMap.playerJoined(p);
		}
	}

	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			playtimeMap.playerQuit(p);
		}
		RedisSaveTask.getInstance().save();
	}

	public <T> T runJedisCommand(JedisCommand<T> jedis) {
		Jedis j = pool.getResource();

		T obj = null;

		try {
			obj = jedis.execute(j);
			pool.returnResource(j);
		}
		catch (JedisException ex) {
			pool.returnBrokenResource(j);
		}
		finally {
			pool.returnResource(j);
		}

		return obj;
	}

	/**
	 * Singleton instance getter
	 * 
	 * @return instance
	 */
	public static FoxtrotPlugin getInstance() {
		return instance;
	}

}
