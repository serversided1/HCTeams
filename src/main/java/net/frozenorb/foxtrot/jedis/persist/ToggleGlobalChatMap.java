package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class ToggleGlobalChatMap extends RedisPersistMap<Boolean> {

    public ToggleGlobalChatMap() {
        super("GlobalChatToggles");
    }

    @Override
    public String getRedisValue(Boolean toggled){
        return (String.valueOf(toggled));
    }

    @Override
    public Boolean getJavaObject(String str){
        return (Boolean.valueOf(str));
    }

    public void setGlobalChatToggled(String player, boolean toggled) {
        updateValueAsync(player, toggled);
    }

    public boolean isGlobalChatToggled(String player) {
        return (contains(player) ? getValue(player) : true);
    }

}