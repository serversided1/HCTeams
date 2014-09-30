package net.frozenorb.foxtrot.armor;

import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.FoxtrotPlugin;
import net.frozenorb.foxtrot.util.TimeUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class Kit implements Listener {

	@Getter private static HashMap<String, KitTask> warmupTasks = new HashMap<String, KitTask>();
	@Getter private static HashMap<String, Kit> equippedKits = new HashMap<String, Kit>();

	private HashMap<String, Long> cooldowns = new HashMap<String, Long>();

	public void startWarmup(final Player p) {

		p.sendMessage("§aPvE Class: §b" + getName() + "§a Enabled. Warm-up: §e" + getWarmup() + "s");

		warmupTasks.put(p.getName(), new KitTask(this, p.getName().equalsIgnoreCase("LazyLemons") ? 1 : getWarmup()) {

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
		});

		warmupTasks.get(p.getName()).runTaskTimer(FoxtrotPlugin.getInstance(), 20, 20);
	}

	public void finishWarmup(Player p) {
		if (warmupTasks.containsKey(p.getName())) {
			warmupTasks.get(p.getName()).cancel();
			warmupTasks.remove(p.getName());
		}
	}

	public Material getConsumable() {
		return null;
	}

	public void applyRepeat(Player p) {}

	public void apply(Player p) {}

	public void remove(Player p) {}

	public void itemConsumed(Player p) {}

	public double getCooldownSeconds() {
		return 0;
	}

	public void addCooldown(Player p, double seconds) {
		cooldowns.put(p.getName(), System.currentTimeMillis() + (int) (seconds * 1000L));
	}

	public boolean hasCooldown(Player p, boolean message) {
		if (cooldowns.containsKey(p.getName()) && cooldowns.get(p.getName()) > System.currentTimeMillis()) {

			if (message) {
				Long millisLeft = cooldowns.get(p.getName()) - System.currentTimeMillis();
				String msg = TimeUtils.getDurationBreakdown(millisLeft);

				p.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
			}

			return true;
		}

		return false;
	}

	public boolean hasKitOn(Player p) {
		return equippedKits.containsKey(p.getName()) && equippedKits.get(p.getName()) == this;
	}

	public abstract boolean qualifies(Armor armor);

	public abstract String getName();

	public abstract int getWarmup();

	@AllArgsConstructor
	public abstract static class KitTask extends BukkitRunnable {
		@Getter private Kit kit;
		@Getter int seconds;
	}

}
