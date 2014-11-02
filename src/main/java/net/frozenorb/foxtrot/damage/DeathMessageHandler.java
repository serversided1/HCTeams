package net.skylandhub.SkylandCommons.damage;

import net.skylandhub.SkylandCommons.SkylandCommons;
import net.skylandhub.SkylandCommons.damage.objects.Damage;
import net.skylandhub.SkylandCommons.damage.listeners.DamageListener;
import net.skylandhub.SkylandCommons.damage.trackers.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by macguy8 on 10/3/2014.
 */
public class DeathMessageHandler {

    //***************************//

    private static Map<String, List<Damage>> damage = new HashMap<String, List<Damage>>();

    //***************************//

    public static void init() {
        SkylandCommons.getInstance().getServer().getPluginManager().registerEvents(new DamageListener(), SkylandCommons.getInstance());

        SkylandCommons.getInstance().getServer().getPluginManager().registerEvents(new GeneralTracker(), SkylandCommons.getInstance());
        SkylandCommons.getInstance().getServer().getPluginManager().registerEvents(new PVPTracker(), SkylandCommons.getInstance());
        SkylandCommons.getInstance().getServer().getPluginManager().registerEvents(new EntityTracker(), SkylandCommons.getInstance());
        SkylandCommons.getInstance().getServer().getPluginManager().registerEvents(new FallTracker(), SkylandCommons.getInstance());
        SkylandCommons.getInstance().getServer().getPluginManager().registerEvents(new ArrowTracker(), SkylandCommons.getInstance());
        SkylandCommons.getInstance().getServer().getPluginManager().registerEvents(new VoidTracker(), SkylandCommons.getInstance());
    }

    //***************************//

    public static List<Damage> getDamage(Player player) {
        return (damage.get(player.getName()));
    }

    public static void addDamage(Player player, Damage addedDamage) {
        if (!damage.containsKey(player.getName())) {
            damage.put(player.getName(), new ArrayList<Damage>());
        }

        damage.get(player.getName()).add(addedDamage);
    }

    public static void clearDamage(Player player) {
        damage.remove(player.getName());
    }

    //***************************//

}