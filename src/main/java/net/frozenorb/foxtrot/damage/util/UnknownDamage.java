package net.frozenorb.foxtrot.damage.util;

import net.frozenorb.foxtrot.damage.objects.Damage;
import org.bukkit.ChatColor;

public class UnknownDamage extends Damage {

    //***************************//

    public UnknownDamage(String damaged, double damage) {
        super(damaged, damage);
    }

    //***************************//

    public String getDescription() {
        return ("Unknown");
    }

    public String getDeathMessage() {
        return (ChatColor.GOLD + getDamaged() + ChatColor.RED + " died.");
    }

    //***************************//

}
