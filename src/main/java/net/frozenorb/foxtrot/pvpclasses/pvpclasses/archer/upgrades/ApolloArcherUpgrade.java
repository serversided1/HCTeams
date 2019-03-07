package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.upgrades;

import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.ArcherUpgrade;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ApolloArcherUpgrade implements ArcherUpgrade {

	@Override
	public String getUpgradeName() {
		return "Apollo";
	}

	@Override
	public int getKillsNeeded() {
		return 100;
	}

	@Override
	public short getMaterialData() {
		return 11;
	}

	@Override
	public void onHit(Player shooter, Player victim) {
		victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 1));
		victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20 * 10, 1));
	}

	@Override
	public boolean applies(Player shooter) {
		for (ItemStack itemStack : shooter.getInventory().getArmorContents()) {
			if (itemStack == null || !itemStack.getType().name().contains("LEATHER_")) {
				return false;
			}

			LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

			if (meta.getColor().getRed() != 229 || meta.getColor().getGreen() != 229 || meta.getColor().getBlue() != 51) {
				return false;
			}
		}

		return true;
	}

}
