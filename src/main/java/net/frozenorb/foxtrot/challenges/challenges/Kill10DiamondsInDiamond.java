package net.frozenorb.foxtrot.challenges.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.challenges.KillBasedChallenge;

public class Kill10DiamondsInDiamond extends KillBasedChallenge {

    public Kill10DiamondsInDiamond() {
        super("Kill 10 Diamonds in Diamond", 10);
    }

    @Override
    public boolean counts(Player killer, Player victim) {
        return hasKit(killer, "Diamond") && hasKit(victim, "Diamond");
    }
    
}
