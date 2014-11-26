package net.frozenorb.foxtrot.team.dtr.bitmask;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
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

    // Used in KOTHs & Citadel
    ARCHER_DAMAGE_NORMALIZED(32, "Archer-Damage-Normalized", "Determines if a region has archer damaged normalized"),

    // Used in all roads
    ROAD(64, "Road", "Determines if a region is a road"),

    // Used in Citadel
    CITADEL_TOWN(128, "Citadel-Town", "Determines if a region is part of Citadel Town"),

    // Used in Citadel
    CITADEL_COURTYARD(256, "Citadel-Courtyard", "Determines if a region is part of Citadel Courtyard"),

    // Used in Citadel
    CITADEL_KEEP(512, "Citadel-Keep", "Determines if a region is part of Citadel Keep"),

    // Used in KOTHs
    KOTH(1024, "KOTH", "Determines if a region is a KOTH"),

    HALF_DTR_LOSS(2048, "Half-DTR-Loss", "Determines if a region only takes away 0.5 DTR upon death");

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