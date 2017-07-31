package net.frozenorb.foxtrot.server;

import com.mongodb.BasicDBObject;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Deathban {

    private static Map<String, Integer> deathban = new LinkedHashMap<>();

    static {
        deathban.put("DEFAULT", 240);
        deathban.put("VIP", 120);
        deathban.put("PRO", 90);
        deathban.put("EPIC", 45);
        deathban.put("HIGHROLLER", 45);
    }

    public static void load(BasicDBObject object) {
        deathban.clear();

        for (String key : object.keySet()) {
            deathban.put(key, object.getInt(key));
        }
    }

    public static int getDeathbanSeconds(Player player) {
        int minutes = 0;

        for (Map.Entry<String, Integer> entry : deathban.entrySet()) {
            if (entry.getKey().equals("DEFAULT") || player.hasPermission("inherit." + entry.getKey().toLowerCase())) {
                if (minutes == 0 || minutes > entry.getValue()) {
                    minutes = entry.getValue();
                }
            }
        }

        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }

}
