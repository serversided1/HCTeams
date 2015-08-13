package net.frozenorb.foxtrot.chat;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.listeners.ChatListener;
import net.frozenorb.foxtrot.chat.tasks.SaveCustomPrefixesTask;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatHandler {

    public static final String HIGHROLLER_PREFIX = ChatColor.DARK_PURPLE + "[HighRoller]";
    public static final File CUSTOM_PREFIXES_FILE = new File("customPrefixes.json");

    private Map<UUID, String> customPrefixes = new HashMap<>();

    public ChatHandler() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new ChatListener(), Foxtrot.getInstance());
        (new SaveCustomPrefixesTask()).runTaskTimerAsynchronously(Foxtrot.getInstance(), 5 * 60 * 20, 5 * 60 * 20); // Every 5 minutes.
        reloadCustomPrefixes();
    }

    public void reloadCustomPrefixes() {
        long started = System.currentTimeMillis();

        try {
            if (!CUSTOM_PREFIXES_FILE.exists()) {
                CUSTOM_PREFIXES_FILE.createNewFile();
            }

            BasicDBObject json = (BasicDBObject) JSON.parse(FileUtils.readFileToString(CUSTOM_PREFIXES_FILE));
            customPrefixes.clear();

            for (Map.Entry<String, Object> prefixEntry : ((BasicDBObject) json.get("prefixes")).entrySet()) {
                customPrefixes.put(UUID.fromString(prefixEntry.getKey()), ChatColor.translateAlternateColorCodes('&', prefixEntry.getValue().toString()));
            }

            int loaded = customPrefixes.size();
            long timeElapsed = System.currentTimeMillis() - started;
            Foxtrot.getInstance().getLogger().warning("Loaded " + loaded + " custom chat prefix" + (loaded == 1 ? "" : "es") + " in " + timeElapsed + "ms");
        } catch (Exception e) {
            Foxtrot.getInstance().getLogger().warning("Failed to load custom chat prefixes: " + e.getMessage());
        }
    }

    public void saveCustomPrefixes() {
        try {
            long started = System.currentTimeMillis();

            BasicDBObject json = new BasicDBObject("prefixes", customPrefixes);
            FileUtils.write(CUSTOM_PREFIXES_FILE, qLib.GSON.toJson(new JsonParser().parse(json.toString())));

            int loaded = customPrefixes.size();
            long timeElapsed = System.currentTimeMillis() - started;
            Foxtrot.getInstance().getLogger().warning("Saved " + loaded + " custom chat prefix" + (loaded == 1 ? "" : "es") + " in " + timeElapsed + "ms");
        } catch (Exception e) {
            Foxtrot.getInstance().getLogger().warning("Failed to save custom chat prefixes: " + e.getMessage());
        }
    }

    public boolean hasCustomPrefix(UUID player) {
        return customPrefixes.containsKey(player);
    }

    public String getCustomPrefix(UUID player) {
        if (customPrefixes.containsKey(player)) {
            return customPrefixes.get(player);
        } else {
            return "";
        }
    }

    public void setCustomPrefix(UUID player, String customPrefix) {
        if (customPrefix == null || customPrefix.isEmpty()) {
            customPrefixes.remove(player);
        } else {
            customPrefixes.put(player, customPrefix);
        }
    }

}