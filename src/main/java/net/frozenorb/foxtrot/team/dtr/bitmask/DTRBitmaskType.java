package net.frozenorb.foxtrot.team.dtr.bitmask;

import lombok.Getter;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Location;

/**
 * Created by Colin on 11/12/2014.
 */
public enum DTRBitmaskType {

    // Used in spawns
    SAFE_ZONE(1, "Safe-Zone", "Determines if a region is considered completely safe"),

    // Used in the end spawn
    DENY_REENTRY(2, "Deny-Reentry", "Determines if a region can be reentered"),

    // Used in Citadel
    FIFTEEN_MINUTE_DEATHBAN(4, "15m-Deathban", "Determines if a region has a 15m deathban"),

    // Used in KOTHs
    FIVE_MINUTE_DEATHBAN(8, "5m-Deathban", "Determines if a region has a 5m deathban"),

    // Used in Citadel
    THIRTY_SECOND_ENDERPEARL_COOLDOWN(16, "30s-Enderpearl-Cooldown", "Determines if a region has a 30s enderpearl cooldown"),

    // Used in Citadel
    CITADEL(32, "Citadel", "Determines if a region is part of Citadel"),

    // Used in KOTHs
    KOTH(64, "KOTH", "Determines if a region is a KOTH"),

    // Used in KOTHs & Citadel.
    REDUCED_DTR_LOSS(128, "Reduced-DTR-Loss", "Determines if a region takes away reduced DTR upon death"),

    // Used in various regions.
    NO_ENDERPEARL(256, "No-Enderpearl", "Determines if a region cannot be pearled into");

    @Getter private int bitmask;
    @Getter private String name;
    @Getter private String description;

    DTRBitmaskType(int bitmask, String name, String description) {
        this.bitmask = bitmask;
        this.name = name;
        this.description = description;
    }

    public boolean appliesAt(Location location) {
        Team ownerTo = LandBoard.getInstance().getTeam(location);
        return (ownerTo != null && ownerTo.getOwner() == null && ownerTo.hasDTRBitmask(this));
    }

}