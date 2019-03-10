package net.frozenorb.foxtrot.map.killstreaks.velttypes;

import net.frozenorb.foxtrot.map.killstreaks.PersistentKillstreak;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Invis extends PersistentKillstreak {

    public Invis() {
        super("Invis", 27);
    }
    
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600*20, 1));
    }
    
}
