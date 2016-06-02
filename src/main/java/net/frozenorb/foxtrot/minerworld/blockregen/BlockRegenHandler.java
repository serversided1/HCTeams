package net.frozenorb.foxtrot.minerworld.blockregen;

import com.google.common.collect.Maps;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.BlockVector;

import java.util.Map;

public class BlockRegenHandler {

    private static Map<Material, Integer> regenerationTime = Maps.newHashMap();

    public BlockRegenHandler() {
        // reset all of the blocks that hadn't been regenerated: this should only do anything
        // if the server crashed or was somehow forcefully stopped.
        qLib.getInstance().runRedisCommand((redis) -> {
            for (String key : redis.keys("minerWorld:toRegen:*")) {
                BlockVector vector = qLib.PLAIN_GSON.fromJson(key.split(":")[2], BlockVector.class);
                BlockData data = qLib.PLAIN_GSON.fromJson(redis.get(key), BlockData.class);

                vector.toLocation(Foxtrot.getInstance().getMinerWorldHandler().getWorld()).getBlock()
                        .setTypeIdAndData(data.getType().getId(), data.getData(), false);

                redis.set(key, null);
            }

            return null;
        });

        ConfigurationSection configuration = Foxtrot.getInstance().getConfig().getConfigurationSection("minerWorld.regen");

        for (String key : configuration.getKeys(false)) {
            regenerationTime.put(Material.valueOf(key.toUpperCase()), configuration.getInt("minerWorld.regen." + key));
        }
    }

    public void regen(Block block) {
        int seconds = regenerationTime.get(block.getType());

        BlockVector vector = block.getLocation().toVector().toBlockVector();
        BlockData data = new BlockData(block.getType(), block.getData());

        block.setTypeIdAndData(Material.COBBLESTONE.getId(), (byte) 0, false);

        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> regen0(vector, data), seconds * 20L);

        Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> qLib.getInstance().runRedisCommand((redis) -> {
            redis.set("minerWorld:toRegen:" + qLib.PLAIN_GSON.toJson(vector), qLib.PLAIN_GSON.toJson(data));
            return null;
        }));
    }

    private void regen0(BlockVector vector, BlockData data) {
        vector.toLocation(Foxtrot.getInstance().getMinerWorldHandler().getWorld()).getBlock()
                .setTypeIdAndData(data.getType().getId(), data.getData(), false);

        Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> qLib.getInstance().runRedisCommand((redis) -> {
            redis.set("minerWorld:toRegen:" + qLib.PLAIN_GSON.toJson(vector), null);
            return null;
        }));
    }

    public static boolean shouldRegen(Material material) {
        return regenerationTime.containsKey(material);
//        return material == Material.COAL_ORE || material == Material.IRON_ORE || material == Material.DIAMOND_ORE;
    }

}