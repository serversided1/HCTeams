package net.frozenorb.foxtrot.challenges.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.challenges.KillBasedChallenge;

public class Kill10ArchersInDiamond extends KillBasedChallenge {

    public Kill10ArchersInDiamond() {
        super("Kill 10 Archers in Diamond", 10);
    }

    @Override
    public boolean counts(Player killer, Player victim) {
        return hasKit(killer, "Diamond") && hasKit(victim, "Archer");
    }
    
    
    
}
