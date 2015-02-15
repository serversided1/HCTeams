package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.jedis.RedisPersistMap;

public class ToggleLightningMap extends RedisPersistMap<Boolean> {

    public ToggleLightningMap() {
        super("LightningToggles", "Lightning");
    }

    @Override
    public String getRedisValue(Boolean toggled){
        return (String.valueOf(toggled));
    }

    @Override
    public Boolean getJavaObject(String str){
        return (Boolean.valueOf(str));
    }

    @Override
    public Object getMongoValue(Boolean toggled) {
        return (toggled);
    }

    public void setLightningToggled(String player, boolean toggled) {
        updateValueAsync(player, toggled);
    }

    public boolean isLightningToggled(String player) {
        return (contains(player) ? getValue(player) : true);
    }

}