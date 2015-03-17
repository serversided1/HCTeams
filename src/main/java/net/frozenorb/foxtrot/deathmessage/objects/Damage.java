package net.frozenorb.foxtrot.deathmessage.objects;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.UUIDUtils;
import org.bukkit.ChatColor;

public abstract class Damage {

    @Getter private String damaged;
    @Getter private double damage;
    @Getter private long time;

    public Damage(String damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public abstract String getDeathMessage();

    public String wrapName(String player) {
        return (ChatColor.RED + player + ChatColor.DARK_RED + "[" + Foxtrot.getInstance().getKillsMap().getKills(UUIDUtils.uuid(player)) + "]" + ChatColor.YELLOW);
    }

}