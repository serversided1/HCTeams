package net.frozenorb.foxtrot.map.killstreaks;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PersistentKillstreak {
    
    @Getter private String name;
    @Getter private int killsRequired;
    
    public boolean matchesExactly(int kills) {
        return kills == killsRequired;
    }
    
    public boolean check(int count) {
        return killsRequired <= count;
    }
    
    public void apply(Player player) {}
    
}
