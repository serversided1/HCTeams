package net.frozenorb.foxtrot.armor.kits;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.frozenorb.foxtrot.armor.Armor;
import net.frozenorb.foxtrot.armor.ArmorMaterial;
import net.frozenorb.foxtrot.armor.Kit;

public class Miner extends Kit {

	@Override
	public boolean qualifies(Armor armor) {
		return armor.isFullSet(ArmorMaterial.IRON, false, false);
	}

	@Override
	public String getName() {
		return "Miner";
	}

	@Override
	public void apply(Player p) {
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
		p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));

	}

	@Override
	public void remove(Player p) {
		p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		p.removePotionEffect(PotionEffectType.FAST_DIGGING);

	}

}
