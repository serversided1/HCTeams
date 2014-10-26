package net.frozenorb.foxtrot.jedis.persist;

import java.util.HashMap;

import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class PlaytimeMap extends RedisPersistMap<Long> {

	private HashMap<String, Long> joinDate = new HashMap<String, Long>();

	public PlaytimeMap() {
		super("player_playtime_seconds");
	}

	@Override
	public String getRedisValue(Long t) {
		return t + "";
	}

	@Override
	public Long getJavaObject(String str) {
		return Long.parseLong(str);
	}

	public void playerJoined(Player p) {
		joinDate.put(p.getName(), System.currentTimeMillis());
	}

	public void playerQuit(Player p) {
		Long l = getValue(p.getName().toLowerCase());

		if (l == null) {
			l = 0L;
		}

		updateValue(p.getName().toLowerCase(), l + (System.currentTimeMillis() - joinDate.get(p.getName())) / 1000);
	}

    public long getCurrentSession(String player){
        if(joinDate.containsKey(player)){
            return System.currentTimeMillis() - joinDate.get(player);
        }

        return 0L;
    }
}
