package net.frozenorb.foxtrot.challenges.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.challenges.KillBasedChallenge;

public class Kill10BardsInDiamond extends KillBasedChallenge {

    public Kill10BardsInDiamond() {
        super("Kill 10 Bards in Diamond", 10);
    }

    @Override
    public boolean counts(Player killer, Player victim) {
        return hasKit(killer, "Diamond") && hasKit(victim, "Bard");
    }
    
}
