package net.frozenorb.foxtrot.armor;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Kit {

	@Getter private static HashMap<String, KitTask> warmupTasks = new HashMap<String, KitTask>();
	@Getter private static HashMap<String, Kit> equippedKits = new HashMap<String, Kit>();

	public void startWarmup(final Player p) {

		p.sendMessage("§aPvE Class: §b" + getName() + "§a Enabled. Warm-up: §e60s");

		warmupTasks.put(p.getName(), new KitTask(new BukkitRunnable() {

			int seconds = 60;

			@Override
			public void run() {
				seconds--;

				if (!p.isOnline()) {

					cancel();
					p.sendMessage("§b" + getName() + " §aEnabled");
					warmupTasks.remove(p.getName());

				}

				if (seconds == 0) {
					apply(p);
					equippedKits.put(p.getName(), Kit.this);
					p.sendMessage("§b" + getName() + " §aEnabled");
					cancel();
					warmupTasks.remove(p.getName());
				}
			}
		}, this));

		warmupTasks.get(p.getName()).getBukkitRunnable().runTaskTimer(FoxtrotPlugin.getInstance(), 20, 20);
	}

	public void finishWarmup(Player p) {
		if (warmupTasks.containsKey(p.getName())) {
			warmupTasks.get(p.getName()).getBukkitRunnable().cancel();
			warmupTasks.remove(p.getName());
		}
	}

	public abstract boolean qualifies(Armor armor);

	public abstract String getName();

	public abstract void apply(Player p);

	public abstract void remove(Player p);

	@AllArgsConstructor
	@Data
	public static class KitTask {
		private BukkitRunnable bukkitRunnable;
		private Kit kit;
	}

}
