package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.upgrades;

import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.ArcherUpgrade;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MedusaArcherUpgrade implements ArcherUpgrade {

	@Override
	public String getUpgradeName() {
		return "Medusa";
	}

	@Override
	public int getKillsNeeded() {
		return 150;
	}

	@Override
	public short getMaterialData() {
		return 8;
	}

	@Override
	public void onHit(Player shooter, Player victim) {
		victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 1));
	}

	@Override
	public boolean applies(Player shooter) {
		for (ItemStack itemStack : shooter.getInventory().getArmorContents()) {
			if (itemStack == null || !itemStack.getType().name().contains("LEATHER_")) {
				return false;
			}

			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

			if (meta.getColor().getRed() != 76 || meta.getColor().getGreen() != 76 || meta.getColor().getBlue() != 76) {
				return false;
			}
		}

		return true;
	}

}
