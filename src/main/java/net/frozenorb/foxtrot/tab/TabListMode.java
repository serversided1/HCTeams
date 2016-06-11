package net.frozenorb.foxtrot.tab;

import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;

public enum TabListMode {

    VANILLA, DETAILED, DETAILED_WITH_FACTION_INFO;

    public String getName() {
        return WordUtils.capitalizeFully(name().toLowerCase().replace("_", " "));
    }

}
