package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.RedisPersistMap;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by chasechocolate.
 */
public class FishingKitMap extends RedisPersistMap<Integer> {
    public static final String META = "fishingkituses";

    public FishingKitMap() {
        super("fishing_kit_uses");
    }

    @Override
    public String getRedisValue(Integer t){
        return t + "";
    }

    @Override
    public Integer getJavaObject(String str){
        return Integer.parseInt(str);
    }

    public void playerJoined(Player player){
        if(!(contains(player.getName()))){
            player.setMetadata(META, new FixedMetadataValue(FoxtrotPlugin.getInstance(), 0));
            return;
        }

        int value = getValue(player.getName());

        player.setMetadata(META, new FixedMetadataValue(FoxtrotPlugin.getInstance(), value));
    }

    public void playerQuit(Player player){
        updateValue(player.getName(), uses(player));
    }

    public int uses(Player player){
        return player.getMetadata(META).get(0).asInt();
    }
}