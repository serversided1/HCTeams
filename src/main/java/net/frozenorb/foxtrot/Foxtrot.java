package net.frozenorb.foxtrot;

import com.comphenix.protocol.ProtocolLibrary;
import com.mongodb.MongoClient;
import lombok.Getter;
import net.frozenorb.foxtrot.chat.listeners.ChatListener;
import net.frozenorb.foxtrot.citadel.CitadelHandler;
import net.frozenorb.foxtrot.conquest.ConquestHandler;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.koth.KOTHHandler;
import net.frozenorb.foxtrot.listener.*;
import net.frozenorb.foxtrot.map.MapHandler;
import net.frozenorb.foxtrot.nametag.FoxtrotNametagProvider;
import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.foxtrot.persist.maps.*;
import net.frozenorb.foxtrot.persist.maps.statistics.*;
import net.frozenorb.foxtrot.protocol.ClientCommandPacketAdaper;
import net.frozenorb.foxtrot.protocol.SignGUIPacketAdaper;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.scoreboard.FoxtrotScoreboardConfiguration;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.team.commands.team.subclaim.TeamSubclaimCommand;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.mShared.Shared;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.scoreboard.FrozenScoreboardHandler;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class Foxtrot extends JavaPlugin {

    @Getter private static Foxtrot instance;

    @Getter private MongoClient mongoPool;

    @Getter private PvPClassHandler pvpClassHandler;
    @Getter private TeamHandler teamHandler;
    @Getter private ServerHandler serverHandler;
    @Getter private MapHandler mapHandler;
    @Getter private CitadelHandler citadelHandler;
    @Getter private KOTHHandler KOTHHandler;
    @Getter private ConquestHandler conquestHandler;

    @Getter private PlaytimeMap playtimeMap = new PlaytimeMap();
    @Getter private OppleMap oppleMap = new OppleMap();
    @Getter private DeathbanMap deathbanMap = new DeathbanMap();
    @Getter private PvPTimerMap PvPTimerMap = new PvPTimerMap();
    @Getter private KillsMap killsMap = new KillsMap();
    @Getter private ChatModeMap chatModeMap = new ChatModeMap();
    @Getter private FishingKitMap fishingKitMap = new FishingKitMap();
    @Getter private ToggleGlobalChatMap toggleGlobalChatMap = new ToggleGlobalChatMap();
    @Getter private ChatSpyMap chatSpyMap = new ChatSpyMap();
    @Getter private DiamondMinedMap diamondMinedMap = new DiamondMinedMap();
    @Getter private GoldMinedMap goldMinedMap = new GoldMinedMap();
    @Getter private IronMinedMap ironMinedMap = new IronMinedMap();
    @Getter private CoalMinedMap coalMinedMap = new CoalMinedMap();
    @Getter private RedstoneMinedMap redstoneMinedMap = new RedstoneMinedMap();
    @Getter private LapisMinedMap lapisMinedMap = new LapisMinedMap();
    @Getter private EmeraldMinedMap emeraldMinedMap = new EmeraldMinedMap();
    @Getter private FirstJoinMap firstJoinMap = new FirstJoinMap();
    @Getter private LastJoinMap lastJoinMap = new LastJoinMap();
    @Getter private SoulboundLivesMap soulboundLivesMap = new SoulboundLivesMap();
    @Getter private FriendLivesMap friendLivesMap = new FriendLivesMap();
    @Getter private TransferableLivesMap transferableLivesMap = new TransferableLivesMap();
    @Getter private BaseStatisticMap enderPearlsUsedMap = new EnderPearlsUsedMap();
    @Getter private BaseStatisticMap expCollectedMap = new ExpCollectedMap();
    @Getter private BaseStatisticMap itemsRepairedMap = new ItemsRepairedMap();
    @Getter private BaseStatisticMap splashPotionsBrewedMap = new SplashPotionsBrewedMap();
    @Getter private BaseStatisticMap splashPotionsUsedMap = new SplashPotionsUsedMap();

    @Override
    public void onEnable() {
        instance = this;

        try {
            mongoPool = new MongoClient();
        } catch (Exception e) {
            e.printStackTrace();
        }

        (new DTRHandler()).runTaskTimer(this, 20L, 1200L);
        (new RedisSaveTask()).runTaskTimerAsynchronously(this, 1200L, 1200L);
        //(new PacketBorderThread()).start();

        setupHandlers();
        setupListeners();

        Shared.get().getProfileManager().setNametagsEnabled(false);
        FrozenNametagHandler.registerProvider(new FoxtrotNametagProvider());
        FrozenScoreboardHandler.setConfiguration(FoxtrotScoreboardConfiguration.create());

        ProtocolLibrary.getProtocolManager().addPacketListener(new SignGUIPacketAdaper());
        ProtocolLibrary.getProtocolManager().addPacketListener(new ClientCommandPacketAdaper());
    }

    @Override
    public void onDisable() {
        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            getPlaytimeMap().playerQuit(player.getUniqueId(), false);
            player.setMetadata("loggedout", new FixedMetadataValue(this, true));
        }

        for (String playerName : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(playerName).remove(getServer().getPlayerExact(playerName));
        }

        RedisSaveTask.save(false);
        Foxtrot.getInstance().getServerHandler().save();
    }

    private void setupHandlers() {
        teamHandler = new TeamHandler();
        LandBoard.getInstance().loadFromTeams();

        serverHandler = new ServerHandler();
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

}