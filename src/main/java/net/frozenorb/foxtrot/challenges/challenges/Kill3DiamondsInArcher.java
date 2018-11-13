package net.frozenorb.foxtrot.challenges.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.challenges.KillBasedChallenge;

public class Kill3DiamondsInArcher extends KillBasedChallenge {
    
    public Kill3DiamondsInArcher() {
        super("Kill 3 Diamonds in Archer", 3);
    }

    @Override
    public boolean counts(Player killer, Player victim) {
        return hasKit(killer, "Archer") && hasKit(victim, "Diamond");
    }
    
}