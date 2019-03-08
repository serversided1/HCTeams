package net.frozenorb.foxtrot.team.upgrades.menu;

import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.menu.button.ComplexButton;
import net.frozenorb.foxtrot.team.upgrades.menu.button.PurchaseButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUpgradesMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return ChatColor.GOLD + "Team Upgrades";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
		Map<Integer, Button> buttons = new HashMap<>();

		for (TeamUpgrade upgrade : TeamUpgrade.upgrades.values()) {
			buttons.put(buttons.size(), upgrade.isCategory() ? new ComplexButton(upgrade) : new PurchaseButton(team, upgrade));
		}

		return buttons;
	}

	@Override
	public boolean isUpdateAfterClick() {
		return true;
	}

}
