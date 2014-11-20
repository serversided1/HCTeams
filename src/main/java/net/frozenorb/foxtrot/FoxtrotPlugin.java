package net.frozenorb.foxtrot;

import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEntity;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mongodb.MongoClient;
import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.command.CommandHandler;
import net.frozenorb.foxtrot.command.CommandRegistrar;
import net.frozenorb.foxtrot.command.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.command.commands.team.TeamSubclaimCommand;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import net.frozenorb.foxtrot.jedis.persist.*;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.map.MapHandler;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.nms.EntityRegistrar;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.raid.DTRHandler;
import net.frozenorb.foxtrot.scoreboard.ScoreboardHandler;
import net.frozenorb.foxtrot.server.PacketBorder;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.mShared.Shared;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class FoxtrotPlugin extends JavaPlugin {

	private static FoxtrotPlugin instance;

    public static final Random RANDOM = new Random();

	private JedisPool jedisPool;
    @Getter private MongoClient mongoPool;

	@Getter private TeamHandler teamHandler;
	@Getter private ServerHandler serverHandler;
	@Getter private MapHandler mapHandler;
	@Getter private ScoreboardHandler scoreboardHandler;
    @Getter private CitadelHandler citadelHandler;

	@Getter private PlaytimeMap playtimeMap;
	@Getter private OppleMap oppleMap;
	@Getter private DeathbanMap deathbanMap;
	@Getter private PvPTimerMap PvPTimerMap;
	@Getter private KillsMap killsMap;
    @Getter private ChatModeMap chatModeMap;
    @Getter private ToggleLightningMap toggleLightningMap;
    @Getter private FishingKitMap fishingKitMap;
    @Getter private ToggleGlobalChatMap toggleGlobalChatMap;
    @Getter private LastDeathMap lastDeathMap;
    @Getter private ChatSpyMap chatSpyMap;
    @Getter private DiamondMinedMap diamondMinedMap;
    @Getter private GoldMinedMap goldMinedMap;
    @Getter private IronMinedMap ironMinedMap;
    @Getter private CoalMinedMap coalMinedMap;
    @Getter private RedstoneMinedMap redstoneMinedMap;
    @Getter private LapisMinedMap lapisMinedMap;
    @Getter private EmeraldMinedMap emeraldMinedMap;
    @Getter private FirstJoinMap firstJoinMap;
    @Getter private LastJoinMap lastJoinMap;
    @Getter private SoulboundLivesMap soulboundLivesMap;
    @Getter private FriendLivesMap friendLivesMap;
    @Getter private TransferableLivesMap transferableLivesMap;

	@Override
	public void onEnable() {
		try {
			EntityRegistrar.registerCustomEntities();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Shared.get().getProfileManager().setNametagsEnabled(false);

		instance = this;

        // Database init
        try {
            jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
            mongoPool = new MongoClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        KOTHHandler.init();
        CommandHandler.init();
        DeathMessageHandler.init();

		RegionManager.register(this);
		RegionManager.get();

        // DTR regeneration
		new DTRHandler().runTaskTimer(this, 20L, 20L * 60);

        // Redis save
        new BukkitRunnable() {

            public void run() {
                RedisSaveTask.save(false);
            }

        }.runTaskTimerAsynchronously(this, 6000L, 6000L);

		PvPClassHandler pvpClassHandler = new PvPClassHandler();

        pvpClassHandler.runTaskTimer(this, 2L, 2L);
		getServer().getPluginManager().registerEvents(pvpClassHandler, this);

		new CommandRegistrar().register();

		teamHandler = new TeamHandler();
		serverHandler = new ServerHandler();
		scoreboardHandler = new ScoreboardHandler();
        mapHandler = new MapHandler();
        citadelHandler = new CitadelHandler();

        setupPersistence();
        LandBoard.getInstance().loadFromTeams();
        new PacketBorder.BorderThread().start();

        // All the listeners...
        getServer().getPluginManager().registerEvents(new MapListener(), this);
        getServer().getPluginManager().registerEvents(new AntiGlitchListener(), this);
        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BorderListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new CitadelListener(), this);
        getServer().getPluginManager().registerEvents(new CombatLoggerListener(), this);
        getServer().getPluginManager().registerEvents(new CrowbarListener(), this);
        getServer().getPluginManager().registerEvents(new DeathbanListener(), this);
        getServer().getPluginManager().registerEvents(new EnchantmentLimiterListener(), this);
        getServer().getPluginManager().registerEvents(new EnderpearlListener(), this);
        getServer().getPluginManager().registerEvents(new EndListener(), this);
        getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), this);
        getServer().getPluginManager().registerEvents(new FoxListener(), this);
        getServer().getPluginManager().registerEvents(new GoldenAppleListener(), this);
        getServer().getPluginManager().registerEvents(new KOTHListener(), this);
        getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), this);
        getServer().getPluginManager().registerEvents(new PvPTimerListener(), this);
        getServer().getPluginManager().registerEvents(new PotionLimiterListener(), this);
        getServer().getPluginManager().registerEvents(new PortalTrapListener(), this);
        getServer().getPluginManager().registerEvents(new RoadListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnTagListener(), this);
        getServer().getPluginManager().registerEvents(new StaffUtilsListener(), this);
        getServer().getPluginManager().registerEvents(new TeamListener(), this);
        getServer().getPluginManager().registerEvents(new WebsiteListener(), this);

        getServer().getPluginManager().registerEvents(new TeamSubclaimCommand(), this);
        getServer().getPluginManager().registerEvents(new TeamClaimCommand(), this);

		for (Player player : getServer().getOnlinePlayers()) {
			playtimeMap.playerJoined(player.getName());
			NametagManager.reloadPlayer(player);
			player.removeMetadata("loggedout", FoxtrotPlugin.getInstance());
		}

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, WrapperPlayServerOpenSignEntity.TYPE) {

            // No sign GUI when placing death signs.
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
	}

	@Override
	public void onDisable() {
		for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
			playtimeMap.playerQuit(player.getName(), false);
			NametagManager.getTeamMap().remove(player.getName());
			player.setMetadata("loggedout", new FixedMetadataValue(this, true));
		}

		for (String str : PvPClassHandler.getEquippedKits().keySet()) {
			Player player = getServer().getPlayerExact(str);
			PvPClassHandler.getEquippedKits().get(str).remove(player);
		}

		RedisSaveTask.save(false);
        FoxtrotPlugin.getInstance().getServerHandler().save();
        jedisPool.destroy();
	}

	public <T> T runJedisCommand(JedisCommand<T> jedisCommand) {
		Jedis jedis = jedisPool.getResource();
		T result = null;

		try {
			result = jedisCommand.execute(jedis);
			jedisPool.returnResource(jedis);
		} catch (Exception e) {
            e.printStackTrace();

            if (jedis != null) {
                jedisPool.returnBrokenResource(jedis);
                jedis = null;
            }
		} finally {
            if (jedis != null) {
                jedisPool.returnResource(jedis);
            }
		}

		return (result);
	}

	private void setupPersistence() {
		playtimeMap = new PlaytimeMap();
		playtimeMap.loadFromRedis();

		oppleMap = new OppleMap();
		oppleMap.loadFromRedis();

		deathbanMap = new DeathbanMap();
		deathbanMap.loadFromRedis();

		PvPTimerMap = new PvPTimerMap();
        PvPTimerMap.loadFromRedis();

		killsMap = new KillsMap();
		killsMap.loadFromRedis();

        chatModeMap = new ChatModeMap();
        chatModeMap.loadFromRedis();

        toggleLightningMap = new ToggleLightningMap();
        toggleLightningMap.loadFromRedis();

        toggleGlobalChatMap = new ToggleGlobalChatMap();
        toggleGlobalChatMap.loadFromRedis();

        fishingKitMap = new FishingKitMap();
        fishingKitMap.loadFromRedis();

        soulboundLivesMap = new SoulboundLivesMap();
        soulboundLivesMap.loadFromRedis();

        friendLivesMap = new FriendLivesMap();
        friendLivesMap.loadFromRedis();

        transferableLivesMap = new TransferableLivesMap();
        transferableLivesMap.loadFromRedis();

        lastDeathMap = new LastDeathMap();
        lastDeathMap.loadFromRedis();

        chatSpyMap = new ChatSpyMap();
        chatSpyMap.loadFromRedis();

        diamondMinedMap = new DiamondMinedMap();
        diamondMinedMap.loadFromRedis();

        goldMinedMap = new GoldMinedMap();
        goldMinedMap.loadFromRedis();

        ironMinedMap = new IronMinedMap();
        ironMinedMap.loadFromRedis();

        coalMinedMap = new CoalMinedMap();
        coalMinedMap.loadFromRedis();

        redstoneMinedMap = new RedstoneMinedMap();
        redstoneMinedMap.loadFromRedis();

        lapisMinedMap = new LapisMinedMap();
        lapisMinedMap.loadFromRedis();

        emeraldMinedMap = new EmeraldMinedMap();
        emeraldMinedMap.loadFromRedis();

        firstJoinMap = new FirstJoinMap();
        firstJoinMap.loadFromRedis();

        lastJoinMap = new LastJoinMap();
        lastJoinMap.loadFromRedis();
	}

    public List<String> getConsoleLog(){
        List<String> log = new ArrayList<String>();

        try{
            FileInputStream input = new FileInputStream(new File("logs", "latest.log"));
            BufferedReader br = new BufferedReader(new InputStreamReader(input));
            String strLine;

            while((strLine = br.readLine()) != null){
                log.add(strLine);
            }

            br.close();
            input.close();
        } catch(Exception e){
            log.add("Error reading log file!");
        }

        return log;
    }

	public static FoxtrotPlugin getInstance() {
		return instance;
	}

}