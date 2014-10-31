package net.frozenorb.foxtrot.armor.kits;

import net.frozenorb.foxtrot.armor.Armor;
import net.frozenorb.foxtrot.armor.ArmorMaterial;
import net.frozenorb.foxtrot.armor.Kit;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Rogue extends Kit {

	public static final double BACKSTAB_ANGLE_DIFF_MAX = 70;

	@Override
	public boolean qualifies(Armor armor) {
		return armor.isFullSet(ArmorMaterial.CHAINMAIL);
	}

	@Override
	public String getName() {
		return "Rogue";
	}

	@Override
	public void apply(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0), true);
	}

	@Override
	public void remove(Player p) {
		p.removePotionEffect(PotionEffectType.SPEED);
		p.removePotionEffect(PotionEffectType.JUMP);

		p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 2), true);
		p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, 0), true);

	}

	@Override
	public double getCooldownSeconds() {
		return 0.5;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityArrowHit(EntityDamageByEntityEvent e) {
		if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			Player p = (Player) e.getDamager();
			Player vic = (Player) e.getEntity();

			float pYaw = p.getLocation().getYaw();
			float vicYaw = vic.getLocation().getYaw();

			float diff = Math.abs(Math.abs(pYaw) - Math.abs(vicYaw));

			if (p.getItemInHand() != null && p.getItemInHand().getType() == Material.GOLD_SWORD && hasKitOn(p) && !hasCooldown(p, true)) {

				if (diff < BACKSTAB_ANGLE_DIFF_MAX || diff >= 360 - BACKSTAB_ANGLE_DIFF_MAX) {

					p.setItemInHand(new ItemStack(Material.AIR));
					p.playSound(p.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
					p.playEffect(p.getLocation(), Effect.STEP_SOUND, Material.GOLD_SWORD);

					p.getWorld().playEffect(vic.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);

					e.setDamage(0.5D);
					vic.setHealth(Math.max(0.5D, ((Damageable) vic).getHealth() - 7D));
					
					addCooldown(p, getCooldownSeconds());
				} else {
					p.sendMessage(ChatColor.RED + "Backstab failed!");
				}

			}

		}
	}

	@Override
	public int getWarmup() {
		return 5;
	}

}