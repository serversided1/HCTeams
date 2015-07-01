package net.frozenorb.foxtrot.librato;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.MinerClass;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.qlib.librato.LibratoPostEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FoxtrotLibratoListener implements Listener {

    @EventHandler
    public void onLibratoPost(LibratoPostEvent event) {
        int totalPlayersInTeams = 0;
        int teamCount = 0;
        int archerActive = 0;
        int bardActive = 0;
        int minerActive = 0;
        int spawnTagged = 0;
        int inNether = 0;
        int inEnd = 0;

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            teamCount++;
            totalPlayersInTeams += team.getSize();
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

        event.getBatch().addGaugeMeasurement("players.deathbanned.count", Foxtrot.getInstance().getDeathbanMap().getDeathbannedPlayers().size());
        event.getBatch().addGaugeMeasurement("teams.count", teamCount);
        event.getBatch().addGaugeMeasurement("teams.averageSize", teamCount == 0 ? 0 : (totalPlayersInTeams / teamCount));
        event.getBatch().addGaugeMeasurement("pvpclasses.archer.active", archerActive);
        event.getBatch().addGaugeMeasurement("pvpclasses.bard.active", bardActive);
        event.getBatch().addGaugeMeasurement("pvpclasses.miner.active", minerActive);
        event.getBatch().addGaugeMeasurement("players.total", Foxtrot.getInstance().getFirstJoinMap().getAllPlayersSize());
        event.getBatch().addGaugeMeasurement("players.spawnTagged", spawnTagged);
        event.getBatch().addGaugeMeasurement("players.inNether", inNether);
        event.getBatch().addGaugeMeasurement("players.inEnd", inEnd);
    }

}