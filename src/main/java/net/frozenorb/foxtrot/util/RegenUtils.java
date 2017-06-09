package net.frozenorb.foxtrot.util;

import javafx.util.Pair;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.util.Callback;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegenUtils {

    private static final Map<Location, Pair<Material, Byte>> allEntries = new HashMap<>();

    public static void schedule(Block block, int amount, TimeUnit unit, Callback<Block> onRegen) {
        int seconds = (int) unit.toSeconds(amount);

        allEntries.put(block.getLocation(), new Pair<>(block.getType(), block.getData()));

        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            onRegen.callback(block);

            block.setTypeIdAndData(allEntries.get(block.getLocation()).getKey().getId(), allEntries.get(block.getLocation()).getValue(), false);
            allEntries.remove(block.getLocation());
        }, seconds * 20L);
    }

    public static void resetAll() {
        for (Map.Entry<Location, Pair<Material, Byte>> entry : allEntries.entrySet()) {
            entry.getKey().getBlock().setTypeIdAndData(entry.getValue().getKey().getId(), entry.getValue().getValue(), false);
        }
    }

}
