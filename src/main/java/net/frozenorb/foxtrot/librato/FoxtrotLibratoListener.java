package net.frozenorb.foxtrot.librato;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.frozenorb.basic.Basic;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.ServerFakeFreezeTask;
import net.frozenorb.foxtrot.chat.ChatHandler;
import net.frozenorb.foxtrot.events.Event;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.MinerClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.qlib.librato.FastLibratoPostEvent;
import net.frozenorb.qlib.librato.LibratoPostEvent;
import net.minecraft.server.v1_7_R4.MinecraftServer;

public class FoxtrotLibratoListener implements Listener {

    @EventHandler
    public void onLibratoPost(LibratoPostEvent event) {
        int kothsActive = 0;
        int archerActive = 0;
        int bardActive = 0;
        int minerActive = 0;
        int spawnTagged = 0;
        int inNether = 0;
        int inEnd = 0;
        int inSafeZone = 0;
        int inCitadel = 0;
        int inConquest = 0;
        int serverMinWorth = 0;
        int serverMaxWorth = 0;
        int chatMessagesSent = ChatHandler.getPublicMessagesSent().getAndSet(0);

        for (Event koth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (koth.isActive()) {
                kothsActive++;
            }
        }

        for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            PvPClass pvpClass = Foxtrot.getInstance().getPvpClassHandler().getPvPClass(player);

            if (pvpClass != null) {
                if (pvpClass instanceof ArcherClass) {
                    archerActive++;
                } else if (pvpClass instanceof BardClass) {
                    bardActive++;
                } else if (pvpClass instanceof MinerClass) {
                    minerActive++;
                }
            }

            if (!player.hasPermission("inherit.mod")) {
                if (player.hasPermission("inherit.pro")) {
                    serverMinWorth += 20;
                    serverMaxWorth += 120;
                } else if (player.hasPermission("inherit.vip")) {
                    serverMinWorth += 10;
                    serverMaxWorth += 75;
                }
            }

            if (SpawnTagHandler.isTagged(player)) {
                spawnTagged++;
            }

            World.Environment world = player.getWorld().getEnvironment();

            if (world == World.Environment.NETHER) {
                inNether++;
            } else if (world == World.Environment.THE_END) {
                inEnd++;
            }

            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                inSafeZone++;
            } else if (DTRBitmask.CITADEL.appliesAt(player.getLocation())) {
                inCitadel++;
            } else if (DTRBitmask.CONQUEST.appliesAt(player.getLocation())) {
                inConquest++;
            }
        }

        event.getBatch().addGaugeMeasurement("bukkit.hcteams.koths.active", kothsActive);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.players.deathbanned.count", Foxtrot.getInstance().getDeathbanMap().getDeathbannedPlayers().size());
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.teams.count", Foxtrot.getInstance().getTeamHandler().getTeams().size());
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.pvpclasses.archer.active", archerActive);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.pvpclasses.bard.active", bardActive);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.pvpclasses.miner.active", minerActive);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.players.totalSeen", Foxtrot.getInstance().getFirstJoinMap().getAllPlayersSize());
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.players.spawnTagged", spawnTagged);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.players.inNether", inNether);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.players.inEnd", inEnd);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.players.inSafeZone", inSafeZone);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.players.inCitadel", inCitadel);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.players.inConquest", inConquest);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.entities", MinecraftServer.getServer().entities);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.activeEntities", MinecraftServer.getServer().activeEntities);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.chat.publicMessages", chatMessagesSent);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.financial.serverMinWorth", serverMinWorth);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.financial.serverMaxWorth", serverMaxWorth);
    }

    @EventHandler
    public void onFastLibratoPost(FastLibratoPostEvent event) {
        long okLatencyResumed = ServerFakeFreezeTask.getOkLatencyResumed();
        boolean frozen = ServerFakeFreezeTask.isFrozen();

        event.getBatch().addGaugeMeasurement("bukkit.hcteams.fakeFreeze.okLatencyResumed", okLatencyResumed < 0 ? 0 : ((System.currentTimeMillis() - okLatencyResumed) / 1000));
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.fakeFreeze.frozen", frozen ? 100 : 0);
        event.getBatch().addGaugeMeasurement("bukkit.hcteams.fakeFreeze.actualFrozen", Basic.getInstance().getServerManager().isFrozen() ? 130 : 0);
    }

}