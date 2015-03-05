package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class ToggleLightningMap extends PersistMap<Boolean> {

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

    public void setLightningToggled(UUID update, boolean toggled) {
        updateValueAsync(update.toString(), toggled);
    }

    public boolean isLightningToggled(UUID check) {
        return (contains(check.toString()) ? getValue(check.toString()) : true);
    }

}