package net.frozenorb.foxtrot.challenges.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.challenges.KillBasedChallenge;

public class Kill3RoguesInBard extends KillBasedChallenge {
    
    public Kill3RoguesInBard() {
        super("Kill 3 Rogues in Bard", 3);
    }

    @Override
    public boolean counts(Player killer, Player victim) {
        return hasKit(killer, "Bard") && hasKit(victim, "Rogue");
    }
    
}