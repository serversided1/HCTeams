package net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.menu;

import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.ArcherUpgrade;
import net.frozenorb.foxtrot.pvpclasses.pvpclasses.archer.menu.button.UpgradeProgressButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArcherUpgradeProgressMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return ChatColor.GOLD +  "Archer Upgrades";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int startAt = 1;

		for (ArcherUpgrade upgrade : ArcherClass.getArcherUpgrades()) {
			buttons.put(startAt, new UpgradeProgressButton(upgrade));
			startAt += 2;
		}

		return buttons;
	}

}
