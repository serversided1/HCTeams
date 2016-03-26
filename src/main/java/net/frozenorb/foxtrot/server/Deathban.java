package net.frozenorb.foxtrot.server;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public enum Deathban {

    DEFAULT("", 4),
    VIP("inherit.vip", 3),
    PRO("inherit.pro", 2);

    @Getter private String permission;
    private int hours;

    public long inSeconds() {
        return TimeUnit.HOURS.toSeconds(hours); // hours -> seconds
    }

    public String inHours() {
        return hours > 1 ? hours + " Hours" : hours + " Hour"; // 1 Hour, 2 Hours, 3 Hours, etc
    }
}
