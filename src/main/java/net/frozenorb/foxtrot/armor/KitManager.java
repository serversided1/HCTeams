package net.frozenorb.foxtrot.armor;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.command.CommandRegistrar;
import lombok.Getter;

public class KitManager {

	@Getter ArrayList<Kit> kits = new ArrayList<Kit>();

	public void loadKits() {
		try {
			for (Class<?> cls : CommandRegistrar.getClassesInPackage("net.frozenorb.foxtrot.armor.kits")) {
				Kit k = (Kit) cls.newInstance();

				kits.add(k);
				Bukkit.getPluginManager().registerEvents(k, FoxtrotPlugin.getInstance());
			}
		}
		catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
