package net.frozenorb.foxtrot.map;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.scoreboard.ScoreboardHandler;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

import java.io.File;

public class MapHandler {

    @Getter private boolean kitMap;
    @Getter private String scoreboardTitle;
    @Getter private String mapStartedString;
    @Getter private double baseLootingMultiplier;
    @Getter private double level1LootingMultiplier;
    @Getter private double level2LootingMultiplier;
    @Getter private double level3LootingMultiplier;
    @Getter private double tradingLootingMultiplier;
    @Getter private double tradingSpawnShopMultiplier;

    public MapHandler() {
        try {
            File mapInfo = new File("mapInfo.json");

            if (!mapInfo.exists()) {
                mapInfo.createNewFile();

                BasicDBObject dbo = new BasicDBObject();
                BasicDBObject looting = new BasicDBObject();

                dbo.put("kitMap", false);
                dbo.put("scoreboardTitle", "&6&lHCTeams &c[Map 1]");
                dbo.put("warzone", 1000);
                dbo.put("border", 3000);
                dbo.put("tradingSpawnShopMod", 1.2D);
                dbo.put("mapStartedString", "Map 3 - Started January 31, 2015");

                looting.put("base", 1D);
                looting.put("level1", 1.2D);
                looting.put("level2", 1.4D);
                looting.put("level3", 2D);
                looting.put("tradingMod", 2D);

                dbo.put("looting", looting);

                FileUtils.write(mapInfo, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbo != null) {
                this.kitMap = dbo.getBoolean("kitMap", false);
                this.scoreboardTitle = ChatColor.translateAlternateColorCodes('&', dbo.getString("scoreboardTitle"));
                ServerHandler.WARZONE_RADIUS = dbo.getInt("warzone", 1000);
                BorderListener.BORDER_SIZE = dbo.getInt("border", 3000);
                this.tradingSpawnShopMultiplier = dbo.getDouble("tradingSpawnShopMod");
                this.mapStartedString = dbo.getString("mapStartedString");

                BasicDBObject looting = (BasicDBObject) dbo.get("looting");

                this.baseLootingMultiplier = looting.getDouble("base");
                this.level1LootingMultiplier = looting.getDouble("level1");
                this.level2LootingMultiplier = looting.getDouble("level2");
                this.level3LootingMultiplier = looting.getDouble("level3");
                this.tradingLootingMultiplier = looting.getDouble("tradingMod");
            }
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

}