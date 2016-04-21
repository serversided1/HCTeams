package net.frozenorb.foxtrot.server;

import com.mongodb.BasicDBObject;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public enum Deathban {

    DEFAULT("", 240),
    VIP("inherit.vip", 120),
    PRO("inherit.pro", 90);

    @Getter private final String permission;
    private int minutes;

    public long inSeconds() {
        return TimeUnit.MINUTES.toSeconds(minutes); // hours -> seconds
    }

    public String inHours() {
        int hours = toHours();

        return hours > 1 ? hours + " Hours" : hours + " Hour"; // 1 Hour, 2 Hours, 3 Hours, etc
    }

    private int toHours() {
        return (int) TimeUnit.MINUTES.toHours(minutes);
    }

    public static void load(BasicDBObject object) {
        for (String key : object.keySet()) {
            valueOf(key).minutes = object.getInt(key);
        }
    }

}
