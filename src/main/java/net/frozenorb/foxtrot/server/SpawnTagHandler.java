package net.frozenorb.foxtrot.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

@RequiredArgsConstructor
public class SpawnTagHandler {

    public static final int MAX_SPAWN_TAG = 60;
    @Getter private static HashMap<String, Long> spawnTags = new HashMap<String, Long>();

    public static void removeTag(Player player) {
        spawnTags.remove(player.getName());
    }

    public static void addSeconds(Player player, int seconds) {
        if (!FoxtrotPlugin.getInstance().getServerHandler().isEOTW() && DTRBitmaskType.SAFE_ZONE.appliesAt(player.getLocation())) {
            return;
        }

        if (isTagged(player)) {
            int secondsTaggedFor = (int) ((spawnTags.get(player.getName()) - System.currentTimeMillis()) / 1000L);
            int newSeconds = Math.min(secondsTaggedFor + seconds, MAX_SPAWN_TAG);

            spawnTags.put(player.getName(), System.currentTimeMillis() + (newSeconds * 1000L));
        } else {
            player.sendMessage(ChatColor.YELLOW + "You have been spawn-tagged for §c" + seconds + " §eseconds!");
            spawnTags.put(player.getName(), System.currentTimeMillis() + (seconds * 1000L));
        }
    }

    public static long getTag(Player player) {
        return (spawnTags.get(player.getName()) - System.currentTimeMillis());
    }

    public static boolean isTagged(Player player) {
        return spawnTags.containsKey(player.getName()) && spawnTags.get(player.getName()) > System.currentTimeMillis();
    }

}