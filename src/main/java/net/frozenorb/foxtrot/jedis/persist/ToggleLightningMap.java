package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.RedisPersistMap;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

/**
 * Created by chasechocolate.
 */
public class ToggleLightningMap extends RedisPersistMap<Boolean> {
    public static final String META = "lightningdisabled";

    public static final MetadataValue META_OBJ = new FixedMetadataValue(FoxtrotPlugin.getInstance(), true);

    public ToggleLightningMap() {
        super("toggle_lightning");
    }

    @Override
    public String getRedisValue(Boolean t){
        return t + "";
    }

    @Override
    public Boolean getJavaObject(String str){
        return Boolean.parseBoolean(str);
    }

    public void playerJoined(Player player){
        if(!(contains(player.getName()))){
            return;
        }

        if(getValue(player.getName())){
            player.setMetadata(META, META_OBJ);
        }
    }

    public void playerQuit(Player player){
        updateValue(player.getName(), player.hasMetadata(META));
    }
}