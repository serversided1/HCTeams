package net.frozenorb.foxtrot.challenges.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.challenges.KillBasedChallenge;

public class Kill5ArchersInBard extends KillBasedChallenge {
    
    public Kill5ArchersInBard() {
        super("Kill 5 Archers in Bard", 5);
    }

    @Override
    public boolean counts(Player killer, Player victim) {
        return hasKit(killer, "Bard") && hasKit(victim, "Archer");
    }
    
}