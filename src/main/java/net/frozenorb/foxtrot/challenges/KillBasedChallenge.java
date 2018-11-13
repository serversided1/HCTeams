package net.frozenorb.foxtrot.challenges;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.pvpclasses.PvPClassHandler;

public abstract class KillBasedChallenge extends Challenge {
    
    public KillBasedChallenge(String name, int countToQualify) {
        super(name, countToQualify);
    }
    
    public abstract boolean counts(Player killer, Player victim);
    
    public boolean hasKit(Player player, String className) {
        Foxtrot.getInstance().getPvpClassHandler();
        PvPClass playerClass = PvPClassHandler.getPvPClass(player);
        if (playerClass != null && (playerClass.getName().equals(className) || playerClass.getName().toLowerCase().contains(className.toLowerCase()))) {
            return true;
        }
        
        if (className.equals("Diamond")) {
            return qualifies(player.getInventory(), "DIAMOND_");
        }
        
        return false;
    }
}

