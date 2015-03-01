package net.frozenorb.foxtrot.deathmessage.objects;

import lombok.Getter;
import mkremins.fanciful.FancyMessage;

public abstract class Damage {

    @Getter private String damaged;
    @Getter private double damage;
    @Getter private long time;

    public Damage(String damaged, double damage) {
        this.damaged = damaged;
        this.damage = damage;
        this.time = System.currentTimeMillis();
    }

    public abstract FancyMessage getDeathMessage();

}