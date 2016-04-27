package net.frozenorb.foxtrot.map.kit.stats;

import lombok.AccessLevel;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.map.kit.stats.command.StatsTopCommand;

import java.util.UUID;

public class StatsEntry {

    @Getter(value = AccessLevel.PROTECTED) private boolean modified;

    @Getter private UUID owner;

    @Getter private int kills;
    @Getter private int deaths;

    @Getter private int killstreak;
    @Getter private int highestKillstreak;

    public StatsEntry(UUID owner) {
        this.owner = owner;
    }

    public void addKill() {
        kills++;
        killstreak++;

        if (highestKillstreak < killstreak) {
            highestKillstreak = killstreak;
        }

        modified = true;
        Foxtrot.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
    }

    public void addDeath() {
        deaths++;
        killstreak = 0;

        modified = true;
        Foxtrot.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
    }

    public void clear() {
        kills = 0;
        deaths = 0;
        killstreak = 0;
        highestKillstreak = 0;

        modified = true;
        Foxtrot.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
    }

    public double getKD() {
        if (getDeaths() == 0) {
            return 0;
        }

        return (double) getKills() / (double) getDeaths();
    }

    public int get(StatsTopCommand.StatsObjective objective) {
        switch (objective) {
            case KILLS:
                return getKills();
            case DEATHS:
                return getDeaths();
            case KD:
                return 0;
            case HIGHEST_KILLSTREAK:
                return getHighestKillstreak();
            default:
                return 0;
        }
    }
}
