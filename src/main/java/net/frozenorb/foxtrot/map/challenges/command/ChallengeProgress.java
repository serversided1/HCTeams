package net.frozenorb.foxtrot.map.challenges.command;

import net.frozenorb.foxtrot.map.challenges.menu.ChallengesMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class ChallengeProgress {

	@Command(names = {"challenge progress", "challenges"}, permission = "")
	public static void progress(Player sender) {
		new ChallengesMenu().openMenu(sender);
	}

}
