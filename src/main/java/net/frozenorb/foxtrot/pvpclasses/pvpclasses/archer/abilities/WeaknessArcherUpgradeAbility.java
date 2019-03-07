package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.abilities;

import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.ArcherUpgradeAbility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WeaknessArcherUpgradeAbility implements ArcherUpgradeAbility {

	@Override
	public void onHit(Player shooter, Player victim) {
		victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 3));
		victim.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 10, 4));
	}

	@Override
	public boolean applies(Player shooter) {
		for (ItemStack itemStack : shooter.getInventory().getArmorContents()) {
			if (itemStack == null || !itemStack.getType().name().contains("LEATHER_")) {
				return false;
			}

			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

			if (meta.getColor().getRed() != 153 || meta.getColor().getGreen() != 51 || meta.getColor().getBlue() != 51) {
				return false;
			}
		}

		return true;
	}

}
