package net.frozenorb.foxtrot.team.upgrades.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrades.TeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.impl.ClaimEffectTeamUpgrade;
import net.frozenorb.foxtrot.team.upgrades.menu.button.PurchaseButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class CategoryUpgradesMenu extends Menu {

	private String title;
	private List<ClaimEffectTeamUpgrade> upgrades;

	@Override
	public String getTitle(Player player) {
		return title;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
		Map<Integer, Button> buttons = new HashMap<>();

		for (TeamUpgrade upgrade : upgrades) {
			buttons.put(buttons.size(), new PurchaseButton(team, upgrade));
		}

		return buttons;
	}

	@Override
	public boolean isUpdateAfterClick() {
		return true;
	}

}
