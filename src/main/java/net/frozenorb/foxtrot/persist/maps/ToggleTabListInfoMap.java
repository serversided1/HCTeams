package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class ToggleTabListInfoMap extends PersistMap<Boolean> {

    public ToggleTabListInfoMap() {
        super("GlobalChatToggles", "GlobalChat");
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

    public void setTabListInfoToggled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isTabListInfoToggled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}