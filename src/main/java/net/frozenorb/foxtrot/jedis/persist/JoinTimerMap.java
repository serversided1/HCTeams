package net.frozenorb.foxtrot.jedis.persist;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class JoinTimerMap extends RedisPersistMap<Long> {
    public static final long PENDING_USE = -10L;

	public JoinTimerMap() {
		super("join_timer");
	}

	@Override
	public Long getJavaObject(String str) {
		return Long.parseLong(str);
	}

	@Override
	public String getRedisValue(Long t) {
		return t + "";
	}

    public void pendingTimer(Player player){
        updateValue(player.getName(), PENDING_USE);
    }

	public void createTimer(Player p, int seconds) {
		updateValue(p.getName(), System.currentTimeMillis() + (seconds * 1000));
        p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "You have now activated your 30 minute PVP protection timer!");
	}

	public boolean hasTimer(Player p) {
        if(contains(p.getName())){
            if (getValue(p.getName()) != null) {
                if(getValue(p.getName()) == PENDING_USE){
                    return false;
                }

                return getValue(p.getName()) > System.currentTimeMillis();
            }
        }
		return false;
	}

}
