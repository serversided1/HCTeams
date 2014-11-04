package net.frozenorb.foxtrot.deathmessage.util;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
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
        return (ChatColor.RED + getDamaged() + ChatColor.DARK_RED + "[" + FoxtrotPlugin.getInstance().getKillsMap().getKills(getDamaged()) + "]" + ChatColor.YELLOW + " died.");
    }

    //***************************//

}
