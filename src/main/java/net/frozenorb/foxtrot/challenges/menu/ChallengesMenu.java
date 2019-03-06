package net.frozenorb.foxtrot.challenges.menu;

import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.challenges.menu.button.ChallengeProgressButton;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import org.bukkit.entity.Player;

public class ChallengesMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Daily Challenges";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(1, new ChallengeProgressButton(Foxtrot.getInstance().getChallengeHandler().getDailyChallenges().get(0)));
		buttons.put(4, new ChallengeProgressButton(Foxtrot.getInstance().getChallengeHandler().getDailyChallenges().get(1)));
		buttons.put(7, new ChallengeProgressButton(Foxtrot.getInstance().getChallengeHandler().getDailyChallenges().get(2)));

		return buttons;
	}

}
