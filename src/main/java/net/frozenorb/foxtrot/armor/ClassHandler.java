package net.frozenorb.foxtrot.armor;

import net.frozenorb.foxtrot.FoxtrotPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("deprecation")
public class ClassHandler extends BukkitRunnable implements Listener {

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
					Kit.getWarmupTasks().get(p.getName()).cancel();

					Kit.getWarmupTasks().remove(p.getName());
				}
			}

			if (Kit.getEquippedKits().containsKey(p.getName())) {
				Kit eq = Kit.getEquippedKits().get(p.getName());

				if (!eq.qualifies(a)) {
					eq.remove(p);
					Kit.getEquippedKits().remove(p.getName());
				} else {
					eq.applyRepeat(p);
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();

		if (e.getPlayer().getItemInHand() != null) {
			for (Kit k : FoxtrotPlugin.getInstance().getKitManager().getKits()) {

				if (k.hasKitOn(p) && k.getConsumable() == e.getPlayer().getItemInHand().getType()) {

					if (!k.hasCooldown(p, true)) {

						if (p.getItemInHand().getAmount() > 1) {
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						} else {
							p.setItemInHand(new ItemStack(Material.AIR));
						}

						k.itemConsumed(p);
						k.addCooldown(p, k.getCooldownSeconds());
					}

				}
			}
		}
	}

}