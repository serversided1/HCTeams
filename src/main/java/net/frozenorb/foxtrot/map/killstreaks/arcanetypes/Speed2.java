package net.frozenorb.foxtrot.map.killstreaks.arcanetypes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.frozenorb.foxtrot.map.killstreaks.PersistentKillstreak;

public class Speed2 extends PersistentKillstreak {

    public Speed2() {
        super("Speed 2", 25);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
    
}
