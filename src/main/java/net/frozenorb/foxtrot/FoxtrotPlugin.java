package net.frozenorb.foxtrot;

import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.armor.ClassTask;
import net.frozenorb.foxtrot.armor.Kit;
import net.frozenorb.foxtrot.armor.KitManager;
import net.frozenorb.foxtrot.command.CommandRegistrar;
import net.frozenorb.foxtrot.diamond.MountainHandler;
import net.frozenorb.foxtrot.game.MinigameManager;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import net.frozenorb.foxtrot.jedis.persist.DeathbanMap;
import net.frozenorb.foxtrot.jedis.persist.JoinTimerMap;
import net.frozenorb.foxtrot.jedis.persist.OppleMap;
import net.frozenorb.foxtrot.jedis.persist.PlaytimeMap;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.nms.EntityRegistrar;
import net.frozenorb.foxtrot.raid.DTRHandler;
import net.frozenorb.foxtrot.server.ServerManager;
import net.frozenorb.foxtrot.team.TeamManager;
import net.frozenorb.foxtrot.visual.BossBarManager;
import net.frozenorb.foxtrot.visual.ScoreboardManager;
import net.frozenorb.foxtrot.visual.TabHandler;
import net.frozenorb.mShared.Shared;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEntity;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

@SuppressWarnings("deprecation")
public class FoxtrotPlugin extends JavaPlugin {
	private static FoxtrotPlugin instance;

	/*
	 * ---- FIELDS ----
	 */
	private JedisPool pool;

	@Getter private TeamManager teamManager;
	@Getter private ServerManager serverManager;
	@Getter private KitManager kitManager;

	@Getter private BossBarManager bossBarManager;
	@Getter private ScoreboardManager scoreboardManager;
	@Getter private MinigameManager minigameManager;

	@Getter private PlaytimeMap playtimeMap;
	@Getter private OppleMap oppleMap;
	@Getter private DeathbanMap deathbanMap;
	@Getter private JoinTimerMap joinTimerMap;

	@Override
	public void onEnable() {
		try {
			EntityRegistrar.registerCustomEntities();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		Shared.get().getProfileManager().setNametagsEnabled(false);

		instance = this;
		pool = new JedisPool(new JedisPoolConfig(), "localhost");
		bossBarManager = new BossBarManager();

		RegionManager.register(this);
		RegionManager.get();

		new DTRHandler().runTaskTimer(this, 20L, 20L * 60);
		new RedisSaveTask().runTaskTimer(this, 13200L, 13200L);
		new ClassTask().runTaskTimer(this, 2L, 2L);
		Bukkit.getScheduler().runTaskTimer(this, bossBarManager, 20L, 20L);
		Bukkit.getScheduler().runTaskTimer(this, new TabHandler(), 0, 10);

		new CommandRegistrar().register();

		teamManager = new TeamManager(this);
		serverManager = new ServerManager();

		minigameManager = new MinigameManager();

		scoreboardManager = new ScoreboardManager();

		setupPersistence();

		kitManager = new KitManager();
		kitManager.loadKits();

		Bukkit.getPluginManager().registerEvents(new BorderListener(), this);
		Bukkit.getPluginManager().registerEvents(new FoxListener(), this);

		for (Player p : Bukkit.getOnlinePlayers()) {
			playtimeMap.playerJoined(p);
			NametagManager.sendPacketsInitialize(p);
			NametagManager.reloadPlayer(p);
		}

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, WrapperPlayServerOpenSignEntity.TYPE) {

			@Override
			public void onPacketSending(PacketEvent event) {

				WrapperPlayServerOpenSignEntity packet = new WrapperPlayServerOpenSignEntity(event.getPacket());
				Player player = event.getPlayer();

				Location loc = new Location(player.getWorld(), packet.getX(), packet.getY(), packet.getZ());

				if (loc.getBlock().getState().hasMetadata("noSignPacket")) {
					event.setCancelled(true);

				}

			}
		});
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, WrapperPlayServerPlayerInfo.TYPE) {

			@Override
			public void onPacketSending(PacketEvent event) {

				WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(event.getPacket());

				if (!packet.getPlayerName().startsWith("$")) {
					event.setCancelled(true);
				} else {
					packet.setPlayerName(packet.getPlayerName().substring(1));
				}

			}
		});

		MountainHandler.load();
	}

	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			playtimeMap.playerQuit(p);
			NametagManager.cleanupTeams(p);
			NametagManager.clear(p);
		}

		for (String str : Kit.getEquippedKits().keySet()) {
			Player p = Bukkit.getPlayerExact(str);

			Kit.getEquippedKits().get(str).remove(p);
		}

		RedisSaveTask.getInstance().save();
		MountainHandler.reset();
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

	private void setupPersistence() {
		playtimeMap = new PlaytimeMap();
		playtimeMap.loadFromRedis();

		oppleMap = new OppleMap();
		oppleMap.loadFromRedis();

		deathbanMap = new DeathbanMap();
		deathbanMap.loadFromRedis();

		joinTimerMap = new JoinTimerMap();
		joinTimerMap.loadFromRedis();
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
