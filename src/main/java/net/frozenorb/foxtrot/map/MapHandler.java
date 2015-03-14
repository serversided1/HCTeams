package net.frozenorb.foxtrot.map;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.qlib.qLib;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class MapHandler {

    @Getter private boolean kitMap;
    @Getter private String scoreboardTitle;
    @Getter private String mapStartedString;
    @Getter private double baseLootingMultiplier;
    @Getter private double level1LootingMultiplier;
    @Getter private double level2LootingMultiplier;
    @Getter private double level3LootingMultiplier;
    @Getter private boolean craftingGopple;
    @Getter private boolean craftingReducedMelon;
    @Getter private int goppleCooldown;

    public MapHandler() {
        try {
            File mapInfo = new File("mapInfo.json");

            if (!mapInfo.exists()) {
                mapInfo.createNewFile();

                BasicDBObject dbObject = new BasicDBObject();
                BasicDBObject looting = new BasicDBObject();
                BasicDBObject crafting = new BasicDBObject();

                dbObject.put("kitMap", false);
                dbObject.put("scoreboardTitle", "&6&lHCTeams &c[Map 1]");
                dbObject.put("mapStartedString", "Map 3 - Started January 31, 2015");
                dbObject.put("warzone", 1000);
                dbObject.put("border", 3000);
                dbObject.put("goppleCooldown", TimeUnit.HOURS.toSeconds(4));

                looting.put("base", 1D);
                looting.put("level1", 1.2D);
                looting.put("level2", 1.4D);
                looting.put("level3", 2D);

                dbObject.put("looting", looting);

                crafting.put("gopple", true);
                crafting.put("reducedMelon", true);

                dbObject.put("crafting", crafting);

                FileUtils.write(mapInfo, qLib.GSON.toJson(new JsonParser().parse(dbObject.toString())));
            }

            BasicDBObject dbObject = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbObject != null) {
                this.kitMap = dbObject.getBoolean("kitMap", false);
                this.scoreboardTitle = ChatColor.translateAlternateColorCodes('&', dbObject.getString("scoreboardTitle"));
                this.mapStartedString = dbObject.getString("mapStartedString");
                ServerHandler.WARZONE_RADIUS = dbObject.getInt("warzone", 1000);
                BorderListener.BORDER_SIZE = dbObject.getInt("border", 3000);
                this.goppleCooldown = dbObject.getInt("goppleCooldown");

                BasicDBObject looting = (BasicDBObject) dbObject.get("looting");

                this.baseLootingMultiplier = looting.getDouble("base");
                this.level1LootingMultiplier = looting.getDouble("level1");
                this.level2LootingMultiplier = looting.getDouble("level2");
                this.level3LootingMultiplier = looting.getDouble("level3");

                BasicDBObject crafting = (BasicDBObject) dbObject.get("crafting");

                this.craftingGopple = crafting.getBoolean("gopple");
                this.craftingReducedMelon = crafting.getBoolean("reducedMelon");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}