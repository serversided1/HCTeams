package net.frozenorb.foxtrot.citadel;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.citadel.events.CitadelCapturedEvent;
import net.frozenorb.foxtrot.citadel.listeners.CitadelListener;
import net.frozenorb.foxtrot.citadel.tasks.CitadelSaveTask;
import net.frozenorb.foxtrot.serialization.serializers.ItemStackSerializer;
import net.frozenorb.foxtrot.serialization.serializers.LocationSerializer;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.bitmask.DTRBitmaskType;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

/**
 * Created by macguy8 on 11/14/2014.
 */
public class CitadelHandler {

    public static final String PREFIX = ChatColor.DARK_PURPLE + "[Citadel]";

    @Getter private ObjectId capper;
    @Getter private Date lootable;

    @Getter private Set<Location> citadelChests = new HashSet<>();
    @Getter private List<ItemStack> citadelLoot = new ArrayList<>();

    public CitadelHandler() {
        loadCitadelInfo();
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new CitadelListener(), FoxtrotPlugin.getInstance());

        (new CitadelSaveTask()).runTaskTimer(FoxtrotPlugin.getInstance(), 0L, 20 * 60 * 5);
    }

    public void loadCitadelInfo() {
        try {
            File citadelInfo = new File("citadelInfo.json");

            if (!citadelInfo.exists()) {
                citadelInfo.createNewFile();
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
                LocationSerializer locationSerializer = new LocationSerializer();

                for (Object chestObj : chests) {
                    BasicDBObject chest = (BasicDBObject) chestObj;
                    citadelChests.add(locationSerializer.deserialize((BasicDBObject) chest.get("location")));
                }

                BasicDBList loot = (BasicDBList) dbo.get("loot");
                ItemStackSerializer itemStackSerializer = new ItemStackSerializer();

                for (Object lootObj : loot) {
                    loot.add(itemStackSerializer.deserialize((BasicDBObject) lootObj));
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

            dbo.put("capper", capper == null ? null : capper.toString());
            dbo.put("lootable", lootable);

            BasicDBList chests = new BasicDBList();
            LocationSerializer locationSerializer = new LocationSerializer();

            for (Location citadelChest : citadelChests) {
                BasicDBObject chest = new BasicDBObject();
                chest.put("location", locationSerializer.serialize(citadelChest));
                chests.add(chest);
            }

            dbo.put("chests", chests);

            BasicDBList loot = new BasicDBList();
            ItemStackSerializer itemStackSerializer = new ItemStackSerializer();

            for (ItemStack lootItem : citadelLoot) {
                loot.add(itemStackSerializer.serialize(lootItem));
            }

            dbo.put("loot", loot);
            citadelInfo.delete();
            FileUtils.write(citadelInfo, FoxtrotPlugin.GSON.toJson(new JsonParser().parse(dbo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCapper(ObjectId capper) {
        this.capper = capper;

        FoxtrotPlugin.getInstance().getServer().getPluginManager().callEvent(new CitadelCapturedEvent(capper));
        saveCitadelInfo();
    }

    public boolean canLootCitadel(Player player) {
        Team team = FoxtrotPlugin.getInstance().getTeamHandler().getPlayerTeam(player.getName());
        return ((team != null && capper.equals(team.getUniqueId())) || System.currentTimeMillis() > lootable.getTime());
    }

    // Credit to http://stackoverflow.com/a/3465656 on StackOverflow.
    private Date generateLootableDate() {
        Calendar date = Calendar.getInstance();
        int diff = Calendar.MONDAY  - date.get(Calendar.DAY_OF_WEEK);

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
        citadelChests.clear();

        for (Team team : FoxtrotPlugin.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() != null) {
                continue;
            }

            if (team.hasDTRBitmask(DTRBitmaskType.CITADEL)) {
                for (Claim claim : team.getClaims()) {
                    Chunk chunk = claim.getChunk();

                    for (BlockState state : chunk.getTileEntities()) {
                        if (state instanceof Chest) {
                            citadelChests.add(state.getLocation());
                        }
                    }
                }
            }
        }
    }

    public void respawnCitadelChests() {
        FoxtrotPlugin.getInstance().getServer().broadcastMessage(PREFIX + " " + ChatColor.GREEN + "Citadel loot chests have respawned!");

        for (Location citadelChest : citadelChests) {
            respawnCitadelChest(citadelChest);
        }
    }

    public void respawnCitadelChest(Location location) {
        BlockState blockState = location.getBlock().getState();

        if (blockState instanceof Chest) {
            Chest chest = (Chest) blockState;
            Team ownerAt = LandBoard.getInstance().getTeam(location);

            if (ownerAt.getOwner() != null) {
                return;
            }

            // Re-checking the bitmask flag ensures there's never a way to get respawning chests in your base
            if (ownerAt.hasDTRBitmask(DTRBitmaskType.CITADEL)) {
                chest.getBlockInventory().clear();

                for (ItemStack loot : getRandomLoot(1)) {
                    chest.getBlockInventory().addItem(loot);
                }
            }
        } else {
            FoxtrotPlugin.getInstance().getLogger().warning("Citadel chest defined at [" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "] isn't a chest!");
        }
    }

    private List<ItemStack> getRandomLoot(int items) {
        List<ItemStack> loot = new ArrayList<ItemStack>();

        for (int i = 0; i < items; i++) {
            ItemStack chosen = citadelLoot.get(FoxtrotPlugin.RANDOM.nextInt(citadelLoot.size()));
            loot.add(chosen);
        }

        return (loot);
    }

}