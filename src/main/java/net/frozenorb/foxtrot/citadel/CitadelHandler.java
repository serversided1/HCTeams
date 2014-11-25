package net.frozenorb.foxtrot.citadel;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.listeners.CitadelListener;
import net.frozenorb.foxtrot.citadel.tasks.CitadelLootTask;
import net.frozenorb.foxtrot.citadel.tasks.CitadelSaveTask;
import net.frozenorb.foxtrot.serialization.serializers.LocationSerializer;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.frozenorb.foxtrot.team.dtr.bitmask.transformer.DTRBitmaskTypeTransformer;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.libs.com.google.gson.GsonBuilder;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

/**
 * Created by macguy8 on 11/14/2014.
 */
public class CitadelHandler {

    public static final String PREFIX = ChatColor.DARK_PURPLE + "[Citadel]";

    @Getter private ObjectId capper;
    @Getter private int level;
    @Getter private Date townLootable;
    @Getter private Date courtyardLootable;
    @Getter private Map<Location, Long> citadelChests = new HashMap<Location, Long>();

    public CitadelHandler() {
        loadCitadelInfo();
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new CitadelListener(), FoxtrotPlugin.getInstance());

        (new CitadelSaveTask()).runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20 * 60 * 5);
        (new CitadelLootTask()).runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20 * 60 * 5);
    }

    public void loadCitadelInfo() {
        try {
            File citadelInfo = new File("citadelInfo.json");

            if (!citadelInfo.exists()) {
                citadelInfo.createNewFile();
                BasicDBObject dbo = new BasicDBObject();

                dbo.put("capper", null);
                dbo.put("level", 0);
                dbo.put("townLootable", getTownLootable());
                dbo.put("courtyardLootable", getCourtyardLootable());
                dbo.put("chests", new BasicDBList());

                FileUtils.write(citadelInfo, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(citadelInfo));

            if (dbo != null) {
                this.capper = dbo.getObjectId("capper");
                this.level = dbo.getInt("level");
                this.townLootable = dbo.getDate("townLootable");
                this.courtyardLootable = dbo.getDate("courtyardLootable");

                BasicDBList chests = (BasicDBList) dbo.get("chests");
                LocationSerializer locationSerializer = new LocationSerializer();

                if (chests != null) {
                    for (Object chestObj : chests) {
                        BasicDBObject chest = (BasicDBObject) chestObj;
                        citadelChests.put(locationSerializer.deserialize((BasicDBObject) chest.get("location")), chest.getLong("time"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCitadelInfo() {
        try {
            File citadelInfo = new File("citadelInfo.json");
            BasicDBObject dbo = new BasicDBObject();

            dbo.put("capper", capper);
            dbo.put("level", level);
            dbo.put("townLootable", townLootable);
            dbo.put("courtyardLootable", courtyardLootable);

            BasicDBList chests = new BasicDBList();
            LocationSerializer locationSerializer = new LocationSerializer();

            for (Map.Entry<Location, Long> citadelChestEntry : citadelChests.entrySet()) {
                BasicDBObject chest = new BasicDBObject();

                chest.put("location", locationSerializer.serialize(citadelChestEntry.getKey()));
                chest.put("time", citadelChestEntry.getValue());

                chests.add(chest);
            }

            dbo.put("chests", chests);

            citadelInfo.delete();
            FileUtils.write(citadelInfo, new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCapper(ObjectId capper, int level) {
        this.capper = capper;
        this.level = level;
        this.townLootable = generateTownLootableDate();
        this.courtyardLootable = generateCourtyardLootableDate();

        saveCitadelInfo();
    }

    public boolean canLootCitadelTown(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());
        return ((team != null && team.getUniqueId() == capper) || System.currentTimeMillis() > townLootable.getTime());
    }

    public boolean canLootCitadelCourtyard(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());
        return ((team != null && team.getUniqueId() == capper) || System.currentTimeMillis() > courtyardLootable.getTime());
    }

    public boolean canLootCitadelKeep(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());
        return (team != null && team.getUniqueId() == capper);
    }

    // Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
    private Date generateTownLootableDate() {
        Calendar date = Calendar.getInstance();
        int diff = Calendar.TUESDAY  - date.get(Calendar.DAY_OF_WEEK);

        if (diff <= 0) {
            diff += 7;
        }

        date.add(Calendar.DAY_OF_MONTH, diff);
        date.set(Calendar.HOUR_OF_DAY, 17); // 5 PM server time
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return (date.getTime());
    }

    // Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
    private Date generateCourtyardLootableDate() {
        Calendar date = Calendar.getInstance();
        int diff = Calendar.THURSDAY  - date.get(Calendar.DAY_OF_WEEK);

        if (diff <= 0) {
            diff += 7;
        }

        date.add(Calendar.DAY_OF_MONTH, diff);
        date.set(Calendar.HOUR_OF_DAY, 17); // 5 PM server time
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);

        return (date.getTime());
    }

    public void scanLoot() {
        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() != null) {
                continue;
            }

            if (team.hasDTRBitmask(DTRBitmaskType.CITADEL_TOWN) || team.hasDTRBitmask(DTRBitmaskType.CITADEL_COURTYARD) || team.hasDTRBitmask(DTRBitmaskType.CITADEL_KEEP)) {
                for (Claim claim : team.getClaims()) {
                    for (Location location : new CuboidRegion("Citadel", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                        if (location.getBlock().getType() != Material.CHEST) {
                            continue;
                        }

                        citadelChests.put(location, System.currentTimeMillis());
                    }
                }
            }
        }
    }

    public void tickCitadelChests() {
        for (Map.Entry<Location, Long> citadelChestEntry : citadelChests.entrySet()) {
            if (citadelChestEntry.getValue() > System.currentTimeMillis()) {
                continue;
            }

            generateCitadelChest(citadelChestEntry.getKey());
        }
    }

    public void generateAllCitadelChests() {
        for (Location citadelChest : citadelChests.keySet()) {
            generateCitadelChest(citadelChest);
        }
    }

    public void generateCitadelChest(Location location) {
        BlockState blockState = location.getBlock().getState();

        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;
            Team ownerAt = LandBoard.getInstance().getTeam(location);

            if (ownerAt.getOwner() != null) {
                return;
            }

            // Re-checking the bitmask flags happens for 2 reasons...
            // 1) To get what part of it's in (even though we could be caching)
            // 2) To ensure there's never a way to get respawning chests in your base
            if (ownerAt.hasDTRBitmask(DTRBitmaskType.CITADEL_TOWN)) {
                chest.getBlockInventory().clear();
                generateCitadelTownChest(chest);
            } else if (ownerAt.hasDTRBitmask(DTRBitmaskType.CITADEL_COURTYARD)) {
                chest.getBlockInventory().clear();
                generateCitadelCourtyardChest(chest);
            } else if (ownerAt.hasDTRBitmask(DTRBitmaskType.CITADEL_KEEP)) {
                chest.getBlockInventory().clear();
                generateCitadelKeepChest(chest);
            }
        }
    }

    private void generateCitadelTownChest(Chest chest) {
        chest.getBlockInventory().addItem(new ItemStack(Material.CHEST));
    }

    private void generateCitadelCourtyardChest(Chest chest) {
        chest.getBlockInventory().addItem(new ItemStack(Material.POISONOUS_POTATO));
    }

    private void generateCitadelKeepChest(Chest chest) {
        chest.getBlockInventory().addItem(new ItemStack(Material.DIAMOND));
    }

}