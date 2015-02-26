package net.frozenorb.foxtrot.citadel;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.Utilities.DataSystem.Regioning.CuboidRegion;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.events.CitadelCapturedEvent;
import net.frozenorb.foxtrot.citadel.listeners.CitadelListener;
import net.frozenorb.foxtrot.citadel.tasks.CitadelSaveTask;
import net.frozenorb.foxtrot.serialization.ItemStackSerializer;
import net.frozenorb.foxtrot.serialization.LocationSerializer;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class CitadelHandler {

    public static final String PREFIX = ChatColor.DARK_PURPLE + "[Citadel]";

    @Getter private ObjectId capper;
    @Getter private Date lootable;

    @Getter private Set<Location> citadelChests = new HashSet<>();
    @Getter private List<ItemStack> citadelLoot = new ArrayList<>();

    public CitadelHandler() {
        loadCitadelInfo();
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new CitadelListener(), FoxtrotPlugin.getInstance());

        (new CitadelSaveTask()).runTaskTimerAsynchronously(FoxtrotPlugin.getInstance(), 0L, 20 * 60 * 5);
    }

    public void loadCitadelInfo() {
        try {
            File citadelInfo = new File("citadelInfo.json");

            if (!citadelInfo.exists() && citadelInfo.createNewFile()) {
                BasicDBObject dbo = new BasicDBObject();

                dbo.put("capper", null);
                dbo.put("lootable", new Date());
                dbo.put("chests", new BasicDBList());
                dbo.put("loot", new BasicDBList());

                FileUtils.write(citadelInfo, FoxtrotPlugin.GSON.toJson(new JsonParser().parse(dbo.toString())));
            }

            BasicDBObject dbo = (BasicDBObject) JSON.parse(FileUtils.readFileToString(citadelInfo));

            if (dbo != null) {
                this.capper = dbo.getString("capper") == null ? null : new ObjectId(dbo.getString("capper"));
                this.lootable = dbo.getDate("lootable");

                BasicDBList chests = (BasicDBList) dbo.get("chests");
                BasicDBList loot = (BasicDBList) dbo.get("loot");

                for (Object chestObj : chests) {
                    BasicDBObject chest = (BasicDBObject) chestObj;
                    citadelChests.add(LocationSerializer.deserialize((BasicDBObject) chest.get("location")));
                }

                for (Object lootObj : loot) {
                    citadelLoot.add(ItemStackSerializer.deserialize((BasicDBObject) lootObj));
                }
            }
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

    public void saveCitadelInfo() {
        try {
            File citadelInfo = new File("citadelInfo.json");
            BasicDBObject dbo = new BasicDBObject();

            dbo.put("capper", capper == null ? null : capper.toString());
            dbo.put("lootable", lootable);

            BasicDBList chests = new BasicDBList();
            BasicDBList loot = new BasicDBList();

            for (Location citadelChest : citadelChests) {
                BasicDBObject chest = new BasicDBObject();
                chest.put("location", LocationSerializer.serialize(citadelChest));
                chests.add(chest);
            }

            for (ItemStack lootItem : citadelLoot) {
                loot.add(ItemStackSerializer.serialize(lootItem));
            }

            dbo.put("chests", chests);
            dbo.put("loot", loot);

            citadelInfo.delete();
            FileUtils.write(citadelInfo, FoxtrotPlugin.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            FoxtrotPlugin.getInstance().getBugSnag().notify(e);
            e.printStackTrace();
        }
    }

    public void setCapper(ObjectId capper) {
        this.capper = capper;
        this.lootable = generateLootableDate();

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new CitadelCapturedEvent(capper));
        saveCitadelInfo();
    }

    public boolean canLootCitadel(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());
        return ((team != null && team.getUniqueId().equals(capper)) || System.currentTimeMillis() > lootable.getTime());
    }

    // Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
    private Date generateLootableDate() {
        Calendar date = Calendar.getInstance();
        int diff = Calendar.FRIDAY  - date.get(Calendar.DAY_OF_WEEK);

        if (diff <= 0) {
            diff += 7;
        }

        date.add(Calendar.DAY_OF_MONTH, diff);

        // 11:59 PM
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);

        return (date.getTime());
    }

    public void scanLoot() {
        citadelChests.clear();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() != null) {
                continue;
            }

            if (team.hasDTRBitmask(DTRBitmaskType.CITADEL)) {
                for (Claim claim : team.getClaims()) {
                    for (Location location : new CuboidRegion("Citadel", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                        if (location.getBlock().getType() == Material.CHEST) {
                            citadelChests.add(location);
                        }
                    }
                }
            }
        }
    }

    public void respawnCitadelChests() {
        citadelChests.forEach((chest) -> respawnCitadelChest(chest));
    }

    public void respawnCitadelChest(Location location) {
        BlockState blockState = location.getBlock().getState();

        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;

            chest.getBlockInventory().clear();
            chest.getBlockInventory().addItem(citadelLoot.get(FoxtrotPlugin.RANDOM.nextInt(citadelLoot.size())));
        } else {
            FoxtrotPlugin.getInstance().getLogger().warning("Citadel chest defined at [" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "] isn't a chest!");
        }
    }

}