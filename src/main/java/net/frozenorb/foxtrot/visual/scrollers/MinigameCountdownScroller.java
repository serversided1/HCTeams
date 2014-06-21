package net.frozenorb.foxtrot.visual.scrollers;

import lombok.AllArgsConstructor;
import net.frozenorb.Utilities.Types.Scrollable;
import net.frozenorb.foxtrot.game.Minigame;

@AllArgsConstructor
public class MinigameCountdownScroller implements Scrollable {
	private Minigame minigame;

	@Override
	public String next() {
		int timeLeft = Math.abs(minigame.getCurrentTime());

		String msg = "§6" + minigame.getBCName() + "§e is starting in §d" + timeLeft + "§e seconds";
		return msg;
	}
}
