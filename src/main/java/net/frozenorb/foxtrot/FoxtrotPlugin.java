package net.frozenorb.foxtrot;

import java.util.List;
import java.util.Random;

import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.armor.ClassTask;
import net.frozenorb.foxtrot.armor.Kit;
import net.frozenorb.foxtrot.armor.KitManager;
import net.frozenorb.foxtrot.command.CommandRegistrar;
import net.frozenorb.foxtrot.command.subcommand.subcommands.teamsubcommands.Subclaim;
import net.frozenorb.foxtrot.diamond.MountainHandler;
import net.frozenorb.foxtrot.game.MinigameManager;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import net.frozenorb.foxtrot.jedis.persist.DeathbanMap;
import net.frozenorb.foxtrot.jedis.persist.JoinTimerMap;
import net.frozenorb.foxtrot.jedis.persist.KillsMap;
import net.frozenorb.foxtrot.jedis.persist.OppleMap;
import net.frozenorb.foxtrot.jedis.persist.PlaytimeMap;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.listener.EndListener;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.nms.EntityRegistrar;
import net.frozenorb.foxtrot.raid.DTRHandler;
import net.frozenorb.foxtrot.server.LocationTickStore;
import net.frozenorb.foxtrot.server.ServerManager;
import net.frozenorb.foxtrot.team.TeamManager;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.visual.BossBarManager;
import net.frozenorb.foxtrot.visual.ScoreboardManager;
import net.frozenorb.foxtrot.visual.TabHandler;
import net.frozenorb.mShared.Shared;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEntity;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedWatchableObject;

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
	@Getter private KillsMap killsMap;

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

		LocationTickStore.getInstance().runTaskTimer(this, 1L, 1L);

		new DTRHandler().runTaskTimer(this, 20L, 20L * 60);
		new RedisSaveTask().runTaskTimer(this, 13200L, 13200L);
		new ClassTask().runTaskTimer(this, 2L, 2L);

		Bukkit.getScheduler().runTaskTimer(this, bossBarManager, 20L, 20L);
		Bukkit.getScheduler().runTaskTimer(this, new TabHandler(), 0, 10);

		new CommandRegistrar().register();

		teamManager = new TeamManager(this); 
		LandBoard.getInstance().loadFromTeams();

		serverManager = new ServerManager();

		minigameManager = new MinigameManager();

		scoreboardManager = new ScoreboardManager();

		setupPersistence();

		kitManager = new KitManager();
		kitManager.loadKits();
		
		Bukkit.getPluginManager().registerEvents(new EndListener(), this);
		Bukkit.getPluginManager().registerEvents(new BorderListener(), this);
		Bukkit.getPluginManager().registerEvents(new FoxListener(), this);
		Bukkit.getPluginManager().registerEvents(new Subclaim("", ""), this);

		for (Player p : Bukkit.getOnlinePlayers()) {
			playtimeMap.playerJoined(p);
			NametagManager.sendPacketsInitialize(p);

			NametagManager.reloadPlayer(p);

			p.removeMetadata("loggedout", FoxtrotPlugin.getInstance());

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

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_METADATA) {
			public void onPacketSending(PacketEvent event) {
				try {
					Player observer = event.getPlayer();
					StructureModifier<Entity> entityModifer = event.getPacket().getEntityModifier(observer.getWorld());
					org.bukkit.entity.Entity entity = entityModifer.read(0);
					if (entity != null && observer != entity && entity instanceof LivingEntity && !(entity instanceof EnderDragon && entity instanceof Wither) && (entity.getPassenger() == null || entity.getPassenger() != observer)) {
						event.setPacket(event.getPacket().deepClone());
						StructureModifier<List<WrappedWatchableObject>> watcher = event.getPacket().getWatchableCollectionModifier();
						for (WrappedWatchableObject watch : watcher.read(0)) {
							if (watch.getIndex() == 6) {
								if ((Float) watch.getValue() > 0) {
									watch.setValue(new Random().nextInt((int) ((Damageable) entity).getMaxHealth()) + new Random().nextFloat());
								}
							}
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		MountainHandler.load();
	}

	@Override
	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			playtimeMap.playerQuit(p);
			NametagManager.getTeamMap().remove(p.getName());
			NametagManager.cleanupTeams(p);

			p.setMetadata("loggedout", new FixedMetadataValue(FoxtrotPlugin.getInstance(), true));

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

		killsMap = new KillsMap();
		killsMap.loadFromRedis();
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
