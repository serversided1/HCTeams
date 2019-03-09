package net.frozenorb.foxtrot.challenges.command;

import net.frozenorb.foxtrot.challenges.menu.ChallengesMenu;
import net.frozenorb.qlib.command.Command;
import org.bukkit.entity.Player;

public class ChallengeProgress {

	@Command(names = {"challenge progress", "challenges"}, permission = "")
	public static void progress(Player sender) {
		new ChallengesMenu().openMenu(sender);
	}

}
