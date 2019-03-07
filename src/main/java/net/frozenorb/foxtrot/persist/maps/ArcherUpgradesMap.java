package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.ArcherUpgrades;

public class ArcherUpgradesMap extends PersistMap<ArcherUpgrades> {

	public ArcherUpgradesMap() {
		super("ArcherUpgrades", "KitMap.ArcherUpgrades");
	}

	@Override
	public String getRedisValue(ArcherUpgrades src) {
		return new StringBuilder().append(src.isPoison())
		                          .append(";")
		                          .append(src.isSlowness())
		                          .append(";")
		                          .append(src.isNausea())
		                          .append(";")
		                          .append(src.isWeakness())
		                          .toString();
	}

	@Override
	public ArcherUpgrades getJavaObject(String str) {
		String[] split = str.split(";");

		ArcherUpgrades upgrades = new ArcherUpgrades();
		upgrades.setPoison(Boolean.valueOf(split[0]));
		upgrades.setSlowness(Boolean.valueOf(split[1]));
		upgrades.setNausea(Boolean.valueOf(split[2]));
		upgrades.setWeakness(Boolean.valueOf(split[3]));

		return upgrades;
	}

	@Override
	public Object getMongoValue(ArcherUpgrades src) {
		return getRedisValue(src);
	}

}
