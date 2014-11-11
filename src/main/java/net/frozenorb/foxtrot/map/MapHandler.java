package net.frozenorb.foxtrot.map;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.enchantments.Enchantment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by macguy8 on 11/8/2014.
 */
public class MapHandler {

    @Getter private String scoreboardTitle;
    @Getter private Map<Enchantment, Integer> maxEnchantments = new HashMap<Enchantment, Integer>();

    public MapHandler() {
        try {
            File mapInfo = new File("mapInfo.json");

            if (!mapInfo.exists()) {
                mapInfo.createNewFile();

                BasicDBObject dbo = new BasicDBObject();
                BasicDBObject enchants = new BasicDBObject();

                dbo.put("scoreboardTitle", "&6&lHCTeams &c[Map 1]");
                dbo.put("warzone", 1000);
                dbo.put("border", 3000);

                enchants.put("PROTECTION_FALL", 4);
                enchants.put("ARROW_DAMAGE", 2);
                enchants.put("ARROW_INFINITE", 1);
                enchants.put("DIG_SPEED", 5);
                enchants.put("DURABILITY", 3);
                enchants.put("LOOT_BONUS_BLOCKS", 3);
                enchants.put("LOOT_BONUS_MOBS", 3);
                enchants.put("SILK_TOUCH", 1);
                enchants.put("LUCK", 3);
                enchants.put("LURE", 3);

                dbo.put("enchants", enchants);

                FileUtils.write(mapInfo, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(mapInfo));

            if (dbo != null) {
                this.scoreboardTitle = ChatColor.translateAlternateColorCodes('&', dbo.getString("scoreboardTitle"));
                ServerHandler.WARZONE_RADIUS = dbo.getInt("warzone");
                BorderListener.BORDER_SIZE = dbo.getInt("border");

                for (Map.Entry<String, Object> enchant : ((BasicDBObject) dbo.get("enchants")).entrySet()) {
                    maxEnchantments.put(Enchantment.getByName(enchant.getKey()), (Integer) enchant.getValue());
                    FoxtrotPlugin.getInstance().getLogger().info("Loaded max enchantment: " + enchant.getKey() + " @ Level " + enchant.getValue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}