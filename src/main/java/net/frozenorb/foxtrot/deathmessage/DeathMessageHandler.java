package net.frozenorb.foxtrot.deathmessage;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.deathmessage.listeners.DamageListener;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.trackers.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by macguy8 on 10/3/2014.
 */
public class DeathMessageHandler {

    private static Map<String, List<Damage>> damage = new HashMap<String, List<Damage>>();

    public static void init() {
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new DamageListener(), FoxtrotPlugin.getInstance());

        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new GeneralTracker(), FoxtrotPlugin.getInstance());
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new PVPTracker(), FoxtrotPlugin.getInstance());
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new EntityTracker(), FoxtrotPlugin.getInstance());
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new FallTracker(), FoxtrotPlugin.getInstance());
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new ArrowTracker(), FoxtrotPlugin.getInstance());
        FoxtrotPlugin.getInstance().getServer().getPluginManager().registerEvents(new VoidTracker(), FoxtrotPlugin.getInstance());
    }

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

}