package net.frozenorb.foxtrot.citadel.enums;

public enum CitadelLootType {

    TOWN_LEVEL1,
    COURTYARD_LEVEL1,
    KEEP_LEVEL1,
    TOWN_LEVEL2,
    COURTYARD_LEVEL2,
    KEEP_LEVEL2,
    TOWN_LEVEL3,
    COURTYARD_LEVEL3,
    KEEP_LEVEL3;

    public static CitadelLootType getTown(int level) {
        return (valueOf("TOWN_LEVEL" + level));
    }

    public static CitadelLootType getCourtyard(int level) {
        return (valueOf("COURTYARD_LEVEL" + level));
    }

    public static CitadelLootType getKeep(int level) {
        return (valueOf("KEEP_LEVEL" + level));
    }

}