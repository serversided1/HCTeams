package net.frozenorb.foxtrot.armor;

import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.armor.kits.Archer;
import net.frozenorb.foxtrot.armor.kits.Bard;
import net.frozenorb.foxtrot.armor.kits.Miner;
import net.frozenorb.foxtrot.armor.kits.Rogue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class KitHandler {

	@Getter ArrayList<Kit> kits = new ArrayList<Kit>();

	public void loadKits() {
        kits.add(new Archer());
        kits.add(new Bard());
        kits.add(new Miner());
        kits.add(new Rogue());

        for (Kit kit : kits) {
            Bukkit.getPluginManager().registerEvents(kit, FoxtrotPlugin.getInstance());
        }

        new BukkitRunnable() {

            public void run() {
                for (Player player : FoxtrotPlugin.getInstance().getServer().getOnlinePlayers()) {
                    for (Kit kit : kits) {
                        if (kit.hasKitOn(player)) {
                            kit.apply(player);
                        }
                    }
                }
            }

        }.runTaskTimer(FoxtrotPlugin.getInstance(), 3L, 3L);
    }
}
