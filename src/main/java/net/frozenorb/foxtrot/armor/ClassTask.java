package net.frozenorb.foxtrot.armor;

import net.frozenorb.foxtrot.FoxtrotPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ClassTask extends BukkitRunnable {

	@Override
	public void run() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			Armor a = Armor.of(p);

			if (p.hasMetadata("freshJoin")) {

				if (Kit.getEquippedKits().containsKey(p.getName())) {
					Kit eq = Kit.getEquippedKits().get(p.getName());

					eq.remove(p);
					Kit.getEquippedKits().remove(p.getName());

				}
				p.removeMetadata("freshJoin", FoxtrotPlugin.getInstance());
			}

			if (Kit.getWarmupTasks().containsKey(p.getName())) {
				Kit trying = Kit.getWarmupTasks().get(p.getName()).getKit();

				if (!trying.qualifies(a)) {
					Kit.getWarmupTasks().get(p.getName()).getBukkitRunnable().cancel();

					Kit.getWarmupTasks().remove(p.getName());
				}
			}

			if (Kit.getEquippedKits().containsKey(p.getName())) {
				Kit eq = Kit.getEquippedKits().get(p.getName());

				if (!eq.qualifies(a)) {
					eq.remove(p);
					Kit.getEquippedKits().remove(p.getName());
				}

			}

			for (Kit k : FoxtrotPlugin.getInstance().getKitManager().getKits()) {
				if (k.qualifies(a)) {
					if (Kit.getWarmupTasks().containsKey(p.getName()) && Kit.getWarmupTasks().get(p.getName()).getKit() == k) {
						continue;
					}

					if (Kit.getEquippedKits().containsKey(p.getName()) && Kit.getEquippedKits().get(p.getName()) == k) {
						continue;
					}
					k.startWarmup(p);
				}
			}

		}
	}

}
