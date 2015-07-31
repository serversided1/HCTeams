package net.frozenorb.foxtrot.librato;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.koth.KOTH;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.MinerClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.qlib.librato.LibratoPostEvent;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftChunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;

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

        for (KOTH koth : Foxtrot.getInstance().getKOTHHandler().getKOTHs()) {
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

            if (SpawnTagHandler.isTagged(player)) {
                spawnTagged++;
            }

            World.Environment world = player.getWorld().getEnvironment();

            if (world == World.Environment.NETHER) {
                inNether++;
            } else if (world == World.Environment.THE_END) {
                inEnd++;
            }
        }

        for (World world : Foxtrot.getInstance().getServer().getWorlds()) {
            int totalChunks = 0;
            int activeChunks = 0;

            /*try {
                Field sleepingField = net.minecraft.server.v1_7_R4.Chunk.class.getField("sleeping");

                for (Chunk chunk : world.getLoadedChunks().clone()) {
                    net.minecraft.server.v1_7_R4.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
                    boolean sleeping = (Boolean) sleepingField.get(nmsChunk);

                    if (!sleeping) {
                        activeChunks++;
                    }

                    totalChunks++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            event.getBatch().addGaugeMeasurement("worlds." + world.getName().toLowerCase() + ".chunks.total", totalChunks);
            event.getBatch().addGaugeMeasurement("worlds." + world.getName().toLowerCase() + ".chunks.active", activeChunks);
        }

        event.getBatch().addGaugeMeasurement("koths.active", kothsActive);
        event.getBatch().addGaugeMeasurement("players.deathbanned.count", Foxtrot.getInstance().getDeathbanMap().getDeathbannedPlayers().size());
        event.getBatch().addGaugeMeasurement("teams.count", Foxtrot.getInstance().getTeamHandler().getTeams().size());
        event.getBatch().addGaugeMeasurement("pvpclasses.archer.active", archerActive);
        event.getBatch().addGaugeMeasurement("pvpclasses.bard.active", bardActive);
        event.getBatch().addGaugeMeasurement("pvpclasses.miner.active", minerActive);
        event.getBatch().addGaugeMeasurement("players.total", Foxtrot.getInstance().getFirstJoinMap().getAllPlayersSize());
        event.getBatch().addGaugeMeasurement("players.spawnTagged", spawnTagged);
        event.getBatch().addGaugeMeasurement("players.inNether", inNether);
        event.getBatch().addGaugeMeasurement("players.inEnd", inEnd);
        event.getBatch().addGaugeMeasurement("entities", MinecraftServer.getServer().entities);
        event.getBatch().addGaugeMeasurement("activeEntities", MinecraftServer.getServer().activeEntities);
    }

}