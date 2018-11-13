package net.frozenorb.foxtrot.challenges.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.challenges.KillBasedChallenge;

public class Kill3DiamondsInBard extends KillBasedChallenge {
    
    public Kill3DiamondsInBard() {
        super("Kill 3 Diamonds in Bard", 3);
    }

    @Override
    public boolean counts(Player killer, Player victim) {
        return hasKit(killer, "Bard") && hasKit(victim, "Diamond");
    }
    
}