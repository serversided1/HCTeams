package net.frozenorb.foxtrot.raffle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum RaffleAchievementType {

    PLAYER_FIRSTS("Player Firsts", 1),
    DAILY_ACHIEVEMENTS("Daily Achievements", 3),
    LONG_TERM_ACHIEVEMENTS("Long-Term Achievements", 15),
    DONATION_ACHIEVEMENTS("Donation Achievements", 0);

    @Getter private String name;
    @Getter private int entries;

}