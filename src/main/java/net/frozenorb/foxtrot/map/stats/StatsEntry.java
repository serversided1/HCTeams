package net.frozenorb.foxtrot.map.stats;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.map.stats.command.StatsTopCommand;

import java.util.UUID;

public class StatsEntry {

    @Getter(value = AccessLevel.PROTECTED) private boolean modified;

    @Getter private UUID owner;

    @Getter
    @Setter
    private int kills;

    @Getter
    @Setter
    private int deaths;

    @Getter
    @Setter
    private int killstreak;

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
    }

    public void addDeath() {
        deaths++;
        killstreak = 0;

        modified = true;
    }

    public void clear() {
        kills = 0;
        deaths = 0;
        killstreak = 0;
        highestKillstreak = 0;

        modified = true;
    }

    public double getKD() {
        if (getDeaths() == 0) {
            return 0;
        }

        return (double) getKills() / (double) getDeaths();
    }

    public Number get(StatsTopCommand.StatsObjective objective) {
        switch (objective) {
            case KILLS:
                return getKills();
            case DEATHS:
                return getDeaths();
            case KD:
                return getKD();
            case HIGHEST_KILLSTREAK:
                return getHighestKillstreak();
            default:
                return 0;
        }
    }
}
