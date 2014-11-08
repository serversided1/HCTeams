package net.frozenorb.foxtrot;

import com.comphenix.packetwrapper.WrapperPlayServerOpenSignEntity;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mongodb.MongoClient;
import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.RegionManager;
import net.frozenorb.foxtrot.armor.ClassHandler;
import net.frozenorb.foxtrot.armor.Kit;
import net.frozenorb.foxtrot.armor.KitHandler;
import net.frozenorb.foxtrot.command.CommandHandler;
import net.frozenorb.foxtrot.command.CommandRegistrar;
import net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Claim;
import net.frozenorb.foxtrot.command.commands.subcommands.teamsubcommands.Subclaim;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.diamond.MountainHandler;
import net.frozenorb.foxtrot.jedis.JedisCommand;
import net.frozenorb.foxtrot.jedis.RedisSaveTask;
import net.frozenorb.foxtrot.jedis.persist.*;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.nms.EntityRegistrar;
import net.frozenorb.foxtrot.raid.DTRHandler;
import net.frozenorb.foxtrot.server.LocationTickStore;
import net.frozenorb.foxtrot.server.PacketBorder.BorderThread;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.visual.BossBarHandler;
import net.frozenorb.foxtrot.visual.scoreboard.ScoreboardHandler;
import net.frozenorb.mShared.Shared;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.jline.internal.InputStreamReader;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

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
	@Getter private KitHandler kitHandler;

	@Getter private BossBarHandler bossBarHandler;
	@Getter private ScoreboardHandler scoreboardHandler;

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
		jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");

        try {
            mongoPool = new MongoClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

		bossBarHandler = new BossBarHandler();

        KOTHHandler.init();
        CommandHandler.init();
        DeathMessageHandler.init();

		RegionManager.register(this);
		RegionManager.get();

		LocationTickStore.getInstance().runTaskTimer(this, 1L, 1L);

		new DTRHandler().runTaskTimer(this, 20L, 20L * 60);
		new RedisSaveTask().runTaskTimerAsynchronously(this, 6000L, 6000L);

		ClassHandler chandler = new ClassHandler();

		chandler.runTaskTimer(this, 2L, 2L);
		getServer().getPluginManager().registerEvents(chandler, this);

		getServer().getScheduler().runTaskTimer(this, bossBarHandler, 20L, 20L);

		new CommandRegistrar().register();

		teamHandler = new TeamHandler();
		LandBoard.getInstance().loadFromTeams();

		serverHandler = new ServerHandler();
		scoreboardHandler = new ScoreboardHandler();

		setupPersistence();

		new BorderThread().start();

		kitHandler = new KitHandler();
		kitHandler.loadKits();

        getServer().getPluginManager().registerEvents(new AlphaMapListener(), this);
        getServer().getPluginManager().registerEvents(new BorderListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
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
        getServer().getPluginManager().registerEvents(new TeamListener(), this);

        getServer().getPluginManager().registerEvents(new Subclaim(), this);
        getServer().getPluginManager().registerEvents(new Claim(), this);

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

		MountainHandler.load();
	}

	@Override
	public void onDisable() {
		for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
			playtimeMap.playerQuit(player.getName());
			NametagManager.getTeamMap().remove(player.getName());
			player.setMetadata("loggedout", new FixedMetadataValue(this, true));
		}

		for (String str : Kit.getEquippedKits().keySet()) {
			Player player = getServer().getPlayerExact(str);
			Kit.getEquippedKits().get(str).remove(player);
		}

		RedisSaveTask.getInstance().save();
		MountainHandler.reset();
        FoxtrotPlugin.getInstance().getServerHandler().save();
	}

	public <T> T runJedisCommand(JedisCommand<T> jedis) {
		Jedis j = jedisPool.getResource();
		T obj = null;

		try {
			obj = jedis.execute(j);
			jedisPool.returnResource(j);
		} catch(JedisException e){
			jedisPool.returnBrokenResource(j);
		} finally {
			jedisPool.returnResource(j);
		}

		return (obj);
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