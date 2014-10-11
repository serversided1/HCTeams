package net.frozenorb.foxtrot.jedis.persist;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.jedis.RedisPersistMap;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author Connor Hollasch
 * @since 10/9/14
 */
public class ChatModeMap extends RedisPersistMap<Boolean> {

    public ChatModeMap() {
        super("player_chat_mode");
    }

    @Override
    public String getRedisValue(Boolean aBoolean) {
        return aBoolean + "";
    }

    @Override
    public Boolean getJavaObject(String str) {
        return Boolean.valueOf(str);
    }

    public void playerJoined(Player player) {
        if (!(contains(player.getName())))
            return;

        boolean isTeamChat = getValue(player.getName());
        if (isTeamChat)
            player.setMetadata("teamChat", new FixedMetadataValue(FoxtrotPlugin.getInstance(), isTeamChat));
    }

    public void playerQuit(Player player) {
        boolean teamChat = player.hasMetadata("teamChat");

        updateValue(player.getName(), teamChat);
    }
}
