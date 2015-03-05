package net.frozenorb.foxtrot.deathmessage.util;

import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.util.ClickableUtils;
import org.bukkit.ChatColor;

public class UnknownDamage extends Damage {

    public UnknownDamage(String damaged, double damage) {
        super(damaged, damage);
    }

    public FancyMessage getDeathMessage() {
        return (ClickableUtils.deathMessageName(getDamaged()).then(ChatColor.YELLOW + " died."));
    }

}