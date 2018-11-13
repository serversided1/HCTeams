package net.frozenorb.foxtrot.challenges.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.challenges.KillBasedChallenge;

public class Kill5BardsInBard extends KillBasedChallenge {
    
    public Kill5BardsInBard() {
        super("Kill 5 Bards in Bard", 5);
    }

    @Override
    public boolean counts(Player killer, Player victim) {
        return hasKit(killer, "Bard") && hasKit(victim, "Bard");
    }
    
}