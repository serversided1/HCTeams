package net.frozenorb.foxtrot.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Betrayer {

    private final UUID uuid;
    private final UUID addedBy;
    private final String reason;
    private final long time;

    public Betrayer(UUID uuid, UUID addedBy, String reason) {
        this(uuid, addedBy, reason, System.currentTimeMillis());
    }

}
