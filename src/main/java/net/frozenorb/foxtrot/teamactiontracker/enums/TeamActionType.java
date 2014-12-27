package net.frozenorb.foxtrot.teamactiontracker.enums;

import lombok.Getter;

public enum TeamActionType {

    GENERAL("general"),
    KILLS("kills"),
    CONNECTIONS("connections"),
    TEAM_CHAT("teamChat"),
    ALLY_CHAT("allyChat");

    @Getter private String name;

    TeamActionType(String name) {
        this.name = name;
    }

}