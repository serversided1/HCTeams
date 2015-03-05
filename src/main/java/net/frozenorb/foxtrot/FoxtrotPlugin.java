package net.frozenorb.foxtrot;

import com.bugsnag.Client;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mongodb.MongoClient;
import lombok.Getter;
import net.frozenorb.foxtrot.chat.listeners.ChatListener;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.conquest.ConquestHandler;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.events.HourEvent;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.map.MapHandler;
import net.frozenorb.foxtrot.nametag.NametagManager;
import net.frozenorb.foxtrot.nametag.NametagThread;
import net.frozenorb.foxtrot.packetborder.PacketBorderThread;
import net.frozenorb.foxtrot.persist.JedisCommand;
import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.foxtrot.persist.maps.*;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.scoreboard.ScoreboardHandler;
import net.frozenorb.foxtrot.scoreboard.ScoreboardThread;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.team.commands.team.subclaim.TeamSubclaimCommand;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.util.ItemMessage;
import net.frozenorb.mBasic.Basic;
import net.frozenorb.mShared.Shared;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("deprecation")
public class FoxtrotPlugin extends JavaPlugin {

    private static FoxtrotPlugin instance;

    public static final Random RANDOM = new Random();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Getter private ItemMessage itemMessage;

    @Getter private JedisPool jedisPool;
    @Getter private MongoClient mongoPool;
    @Getter private Client bugSnag;

    @Getter private PvPClassHandler pvpClassHandler;
    @Getter private TeamHandler teamHandler;
    @Getter private ServerHandler serverHandler;
    @Getter private MapHandler mapHandler;
    @Getter private ScoreboardHandler scoreboardHandler;
    @Getter private CitadelHandler citadelHandler;
    @Getter private KOTHHandler KOTHHandler;
    @Getter private ConquestHandler conquestHandler;

    @Getter private PlaytimeMap playtimeMap;
    @Getter private OppleMap oppleMap;
    @Getter private DeathbanMap deathbanMap;
    @Getter private PvPTimerMap PvPTimerMap;
    @Getter private KillsMap killsMap;
    @Getter private ChatModeMap chatModeMap;
    @Getter private ToggleLightningMap toggleLightningMap;
    @Getter private FishingKitMap fishingKitMap;
    @Getter private ToggleGlobalChatMap toggleGlobalChatMap;
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
        instance = this;

        Basic.get();

        // Redis fucking dies without this here. I honestly don't even know.
        Thread.currentThread().setContextClassLoader(getClassLoader());

        try {
            jedisPool = new JedisPool(new JedisPoolConfig(), "localhost");
            mongoPool = new MongoClient();
            bugSnag = new Client("424ef6646404116dd57cf0178863fcf6");
            //bugSnag.notify(new RuntimeException("Non-fatal"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Shared.get().getProfileManager().setNametagsEnabled(false);

        new DTRHandler().runTaskTimer(this, 20L, 1200L); // Runs every minute
        new RedisSaveTask().runTaskTimerAsynchronously(this, 6000L, 6000L); // Runs every 5 minutes

        setupHandlers();
        setupPersistence();
        setupListeners();

        itemMessage = new ItemMessage();

        Calendar date = Calendar.getInstance();

        date.set(Calendar.MINUTE, 60);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        (new Timer("Hourly Scheduler")).schedule(new TimerTask() {

            @Override
            public void run() {
                new BukkitRunnable() {

                    public void run() {
                        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new HourEvent(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
                    }

                }.runTask(FoxtrotPlugin.getInstance());
            }

        }, date.getTime(), TimeUnit.HOURS.toMillis(1));

        (new PacketBorderThread()).start();
        (new ScoreboardThread()).start();
        (new NametagThread()).start();

        for (Player player : getServer().getOnlinePlayers()) {
            getPlaytimeMap().playerJoined(player.getUniqueId());
            NametagManager.reloadPlayer(player);
            player.removeMetadata("loggedout", FoxtrotPlugin.getInstance());
        }

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.OPEN_SIGN_ENTITY) {

            // No sign GUI when placing death/KOTH signs.
            @Override
            public void onPacketSending(PacketEvent event) {
                Location location = new Location(event.getPlayer().getWorld(), event.getPacket().getIntegers().read(0), event.getPacket().getIntegers().read(1), event.getPacket().getIntegers().read(2));

                if (location.getBlock().getState().hasMetadata("noSignPacket")) {
                    event.setCancelled(true);
                }
            }

        });

        // NEXT MAP
        Iterator<Recipe> recipeIterator = getServer().recipeIterator();

        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();

            // Disallow the crafting of gopples.
            if (recipe.getResult().getDurability() == (short) 1 && recipe.getResult().getType() == org.bukkit.Material.GOLDEN_APPLE) {
                recipeIterator.remove();
            }

            // Remove vanilla glistering melon recipe
            if (recipe.getResult().getType() == Material.SPECKLED_MELON) {
                recipeIterator.remove();
            }
        }

        // add our glistering melon recipe
        getServer().addRecipe(new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON)).addIngredient(Material.MELON).addIngredient(Material.GOLD_NUGGET));
    }


    @Override
    public void onDisable() {
        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            getPlaytimeMap().playerQuit(player.getUniqueId(), false);
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
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
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

    public void sendOPMessage(String message) {
        for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(message);
            }
        }
    }

    private void setupHandlers() {
        teamHandler = new TeamHandler();
        LandBoard.getInstance().loadFromTeams();

        serverHandler = new ServerHandler();
        scoreboardHandler = new ScoreboardHandler();
        mapHandler = new MapHandler();
        citadelHandler = new CitadelHandler();
        pvpClassHandler = new PvPClassHandler();
        KOTHHandler = new KOTHHandler();
        conquestHandler = new ConquestHandler();

        FrozenCommandHandler.loadCommandsFromPackage(this, "net.frozenorb.foxtrot.citadel");
        FrozenCommandHandler.loadCommandsFromPackage(this, "net.frozenorb.foxtrot.commands");
        FrozenCommandHandler.loadCommandsFromPackage(this, "net.frozenorb.foxtrot.conquest");
        FrozenCommandHandler.loadCommandsFromPackage(this, "net.frozenorb.foxtrot.koth");
        FrozenCommandHandler.loadCommandsFromPackage(this, "net.frozenorb.foxtrot.server");
        FrozenCommandHandler.loadCommandsFromPackage(this, "net.frozenorb.foxtrot.team");

        DeathMessageHandler.init();
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new MapListener(), this);
        getServer().getPluginManager().registerEvents(new AntiGlitchListener(), this);
        getServer().getPluginManager().registerEvents(new BasicPreventionListener(), this);
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
        getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), this);
        getServer().getPluginManager().registerEvents(new PvPTimerListener(), this);
        getServer().getPluginManager().registerEvents(new PotionLimiterListener(), this);
        getServer().getPluginManager().registerEvents(new PortalTrapListener(), this);
        getServer().getPluginManager().registerEvents(new RoadListener(), this);
        getServer().getPluginManager().registerEvents(new SignSubclaimListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnListener(), this);
        getServer().getPluginManager().registerEvents(new SpawnTagListener(), this);
        getServer().getPluginManager().registerEvents(new StaffUtilsListener(), this);
        getServer().getPluginManager().registerEvents(new TeamListener(), this);
        getServer().getPluginManager().registerEvents(new WebsiteListener(), this);

        getServer().getPluginManager().registerEvents(new TeamSubclaimCommand(), this);
        getServer().getPluginManager().registerEvents(new TeamClaimCommand(), this);
    }

    private void setupPersistence() {
        (playtimeMap = new PlaytimeMap()).loadFromRedis();
        (oppleMap = new OppleMap()).loadFromRedis();
        (deathbanMap = new DeathbanMap()).loadFromRedis();
        (PvPTimerMap = new PvPTimerMap()).loadFromRedis();
        (killsMap = new KillsMap()).loadFromRedis();
        (chatModeMap = new ChatModeMap()).loadFromRedis();
        (toggleLightningMap = new ToggleLightningMap()).loadFromRedis();
        (toggleGlobalChatMap = new ToggleGlobalChatMap()).loadFromRedis();
        (fishingKitMap = new FishingKitMap()).loadFromRedis();
        (soulboundLivesMap = new SoulboundLivesMap()).loadFromRedis();
        (friendLivesMap = new FriendLivesMap()).loadFromRedis();
        (transferableLivesMap = new TransferableLivesMap()).loadFromRedis();
        (chatSpyMap = new ChatSpyMap()).loadFromRedis();
        (diamondMinedMap = new DiamondMinedMap()).loadFromRedis();
        (goldMinedMap = new GoldMinedMap()).loadFromRedis();
        (ironMinedMap = new IronMinedMap()).loadFromRedis();
        (coalMinedMap = new CoalMinedMap()).loadFromRedis();
        (redstoneMinedMap = new RedstoneMinedMap()).loadFromRedis();
        (lapisMinedMap = new LapisMinedMap()).loadFromRedis();
        (emeraldMinedMap = new EmeraldMinedMap()).loadFromRedis();
        (firstJoinMap = new FirstJoinMap()).loadFromRedis();
        (lastJoinMap = new LastJoinMap()).loadFromRedis();
    }

    public static FoxtrotPlugin getInstance() {
        return (instance);
    }

}