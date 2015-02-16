package net.frozenorb.foxtrot.nametag;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@Data
@RequiredArgsConstructor
public class TeamInfo {

    @NonNull private String name;
    @NonNull private String prefix;
    @NonNull private String suffix;

    private ScoreboardTeamPacketMod teamAddPacket = new ScoreboardTeamPacketMod(name, prefix, suffix, new ArrayList<String>(), 0);

}