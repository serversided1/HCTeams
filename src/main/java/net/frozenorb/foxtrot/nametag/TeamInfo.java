package net.frozenorb.foxtrot.nametag;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@Data
public class TeamInfo {

    private String name;
    private String prefix;
    private String suffix;

    private ScoreboardTeamPacketMod teamAddPacket;

    public TeamInfo(String name, String prefix, String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;

        teamAddPacket = new ScoreboardTeamPacketMod(name, prefix, suffix, new ArrayList<String>(), 0);
    }

}