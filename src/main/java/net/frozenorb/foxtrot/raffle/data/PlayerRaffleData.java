package net.frozenorb.foxtrot.raffle.data;

import lombok.Data;
import net.frozenorb.foxtrot.raffle.enums.RaffleAchievement;

import java.util.HashMap;
import java.util.Map;

@Data
public class PlayerRaffleData {

    private Map<RaffleAchievement, Integer> progress = new HashMap<RaffleAchievement, Integer>();
    private Map<RaffleAchievement, Long> lastEarned = new HashMap<RaffleAchievement, Long>();
    private Map<Integer, Integer> weekEntries = new HashMap<Integer, Integer>();
    private int totalEntries = 0;

}