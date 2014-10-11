package net.frozenorb.foxtrot.armor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import net.frozenorb.foxtrot.armor.kits.Archer;
import net.frozenorb.foxtrot.armor.kits.Bard;
import net.frozenorb.foxtrot.armor.kits.Miner;
import net.frozenorb.foxtrot.armor.kits.Rogue;
import org.bukkit.Bukkit;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.CommandRegistrar;
import lombok.Getter;

public class KitManager {

	@Getter ArrayList<Kit> kits = new ArrayList<Kit>();

	public void loadKits() {
        kits.add(new Archer());
        kits.add(new Bard());
        kits.add(new Miner());
        kits.add(new Rogue());

        for (Kit kit : kits) {
            Bukkit.getPluginManager().registerEvents(kit, FoxtrotPlugin.getInstance());
        }
    }
}
