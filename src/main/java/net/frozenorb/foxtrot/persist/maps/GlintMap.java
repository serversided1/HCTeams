package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GlintMap extends PersistMap<Boolean> {

    public GlintMap() {
        super("Glint", "Glint");
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

    public void setGlintToggled(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean getGlintToggled(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

    public static void setGlintEnabled(Player player, boolean glint) {
        try {
            //EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            //Field glintField = entityPlayer.getClass().getField("glintEnabled");

            //glintField.set(entityPlayer, glint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}