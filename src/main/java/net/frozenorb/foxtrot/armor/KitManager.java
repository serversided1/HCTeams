package net.frozenorb.foxtrot.armor;

import java.util.ArrayList;

import net.frozenorb.foxtrot.armor.kits.Miner;
import lombok.Getter;

public class KitManager {

	@Getter ArrayList<Kit> kits = new ArrayList<Kit>();

	public void loadKits() {
		kits.add(new Miner());
	}
}
