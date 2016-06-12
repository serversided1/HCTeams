package net.frozenorb.foxtrot.tab;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TabListMode {

    DETAILED("Detailed"),
    DETAILED_WITH_FACTION_INFO("Detailed w/ Faction Info"),
    VANILLA("Vanilla");

    private final String name;

    public String getName() {
        return name;
    }

}
