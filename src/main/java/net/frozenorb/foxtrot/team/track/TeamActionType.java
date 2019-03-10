package net.frozenorb.foxtrot.team.track;

import com.google.common.base.CaseFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TeamActionType {

    // Chat Messages
    ALLY_CHAT_MESSAGE(false),
    TEAM_CHAT_MESSAGE(false),
    OFFICER_CHAT_MESSAGE(false),

    // Financial + Land
    PLAYER_WITHDRAW_MONEY(true),
    PLAYER_DEPOSIT_MONEY(true),
    PLAYER_CLAIM_LAND(true),
    PLAYER_UNCLAIM_LAND(true),
    PLAYER_RESIZE_LAND(true),

    // Create + Delete
    PLAYER_CREATE_TEAM(true),
    PLAYER_DISBAND_TEAM(true),

    // Mutes
    TEAM_MUTE_CREATED(false),
    TEAM_MUTE_EXPIRED(false),

    // Connections
    MEMBER_CONNECTED(true),
    MEMBER_DISCONNECTED(true),

    // Basic
    ANNOUNCEMENT_CHANGED(true),
    HEADQUARTERS_CHANGED(true),
    POWER_FAC_STATUS_CHANGED(true),

    // Invites
    PLAYER_INVITE_SENT(false),
    PLAYER_INVITE_REVOKED(false),

    // Player Ranks
    PLAYER_JOINED(true),
    MEMBER_KICKED(true),
    MEMBER_REMOVED(true),
    LEADER_CHANGED(true),
    PROMOTED_TO_CAPTAIN(true),
    PROMOTED_TO_CO_LEADER(true),
    DEMOTED_FROM_CAPTAIN(true),
    DEMOTED_FROM_CO_LEADER(true),

    // PvP Deaths
    MEMBER_KILLED_ENEMY_IN_PVP(true),
    MEMBER_KILLED_BY_ENEMY_IN_PVP(true),

    // DTR
    MEMBER_DEATH(true),
    TEAM_NOW_RAIDABLE(true),
    TEAM_NO_LONGER_RAIDABLE(true);

    @Getter private boolean loggedToDatabase;

    public String getInternalName() {
        // thanks guava!
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name());
    }

}