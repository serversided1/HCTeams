package net.frozenorb.foxtrot.raffle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public enum RaffleAchievement {

    // Player Firsts
    WELCOME_TO_HCT("Welcome to HCTeams", "Login for the first time", RaffleAchievementType.PLAYER_FIRSTS),
    LUMBERJACK("Lumberjack", "Cut down a tree!", RaffleAchievementType.PLAYER_FIRSTS),
    MAKING_FRIENDS("Making Friends", "Join your first team", RaffleAchievementType.PLAYER_FIRSTS),
    BROKER("Broker", "Claim land for the first time", RaffleAchievementType.PLAYER_FIRSTS),
    FAMILIAR_FACE("Familiar Face", "Achieve 1 hour of playtime", RaffleAchievementType.PLAYER_FIRSTS),
    LOADED("Loaded", "Equip full diamond", RaffleAchievementType.PLAYER_FIRSTS),
    BREAKING_BAD("Breaking Bad", "Brew your first potion", RaffleAchievementType.PLAYER_FIRSTS),
    FIRST_BLOOD("First Blood", "Earn your first kill", RaffleAchievementType.PLAYER_FIRSTS),
    SUPERMAN("Superman", "Consume a super golden apple", RaffleAchievementType.PLAYER_FIRSTS),
    CAPTURER("Capturer", "Capture your first KOTH!", RaffleAchievementType.PLAYER_FIRSTS),

    // Daily Achievements
    EXECUTIONER("Executioner", "Kill a player", RaffleAchievementType.DAILY_ACHIEVEMENTS),
    GUARDIAN_ANGEL("Guardian Angel", "Revive a player", RaffleAchievementType.DAILY_ACHIEVEMENTS),
    VICTORY("Victory", "Capture a KOTH", RaffleAchievementType.DAILY_ACHIEVEMENTS),

    // Long-Term Achievements
    ONE_DAY("1 Day", "Achieve 1 day of playtime", RaffleAchievementType.LONG_TERM_ACHIEVEMENTS),
    DIAMOND_HUNTER("Diamond Hunter", "Mine 500 diamonds", RaffleAchievementType.LONG_TERM_ACHIEVEMENTS, 500),
    THREE_DAYS("3 Days", "Achieve 3 days of playtime", RaffleAchievementType.LONG_TERM_ACHIEVEMENTS),
    BIG_SPENDER("Big Spender", "/team hq 100 times", RaffleAchievementType.LONG_TERM_ACHIEVEMENTS, 100),
    TRUMP("Trump", "Earn 25,000 coins", RaffleAchievementType.LONG_TERM_ACHIEVEMENTS, 25000),
    DRAGON_SLAYER("Dragon Slayer", "Kill the ender dragon", RaffleAchievementType.LONG_TERM_ACHIEVEMENTS, 1),
    CITADEL("Citadel", "Capture Citadel!", RaffleAchievementType.LONG_TERM_ACHIEVEMENTS, 1),

    // Donation Achievements
    PURCHASE_A_LIFE("Purchase a life", "Purchase a life", RaffleAchievementType.DONATION_ACHIEVEMENTS),
    PURCHASE_SUBSCRIBER("Purchase Subscriber", "Purchase Subscriber", RaffleAchievementType.DONATION_ACHIEVEMENTS),
    PURCHASE_VIP("Purchase VIP", "Purchase VIP", RaffleAchievementType.DONATION_ACHIEVEMENTS),
    PURCHASE_PRO("Purchase PRO", "Purchase PRO", RaffleAchievementType.DONATION_ACHIEVEMENTS),
    PURCHASE_HIGHROLLER("Purchase HighRoller", "Purchase HighRoller", RaffleAchievementType.DONATION_ACHIEVEMENTS);

    @NonNull @Getter private String name;
    @NonNull @Getter private String description;
    @NonNull @Getter RaffleAchievementType type;
    @Getter private int maxProgress= -1;

}