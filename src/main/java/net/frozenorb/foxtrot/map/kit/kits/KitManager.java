package net.frozenorb.foxtrot.map.kit.kits;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.qLib;
import org.bukkit.Bukkit;

import java.util.List;

public class KitManager {

    @Getter private List<Kit> kits = Lists.newArrayList();

    public KitManager() {
        // load all kits from local redis
        qLib.getInstance().runRedisCommand((redis) -> {
            for (String key : redis.keys("kit.*")) {
                Kit kit = qLib.PLAIN_GSON.fromJson(redis.get(key), Kit.class);

                kits.add(kit);
            }
            return null;
        });

        // sort kits by name, alphabetically
        kits.sort((first, second) -> first.getName().compareToIgnoreCase(second.getName()));
        Foxtrot.getInstance().getLogger().info("- Kit Manager - Loaded " + kits.size() + " kits.");

        FrozenCommandHandler.registerPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.map.kit.kits.commands");
        FrozenCommandHandler.registerParameterType(Kit.class, new Kit.Type());

        Bukkit.getPluginManager().registerEvents(new KitListener(), Foxtrot.getInstance());
    }

    public Kit get(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }

        return null;
    }

    public Kit getOrCreate(String name) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }

        Kit kit = new Kit(name);
        kits.add(kit);

        return kit;
    }

    public void delete(Kit kit) {
        kits.remove(kit);
    }

    public void save() {
        qLib.getInstance().runRedisCommand((redis) -> {
            for (Kit kit : kits) {
                redis.set("kit." + kit.getName(), qLib.PLAIN_GSON.toJson(kit));
            }
            return null;
        });
    }

}
