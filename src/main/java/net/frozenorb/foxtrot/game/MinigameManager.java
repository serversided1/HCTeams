package net.frozenorb.foxtrot.game;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

/**
 * Class that handles the creation and management of minigames.
 * 
 * @author Kerem Celik
 * 
 */
public class MinigameManager extends SerialDataLoader {
	@Getter private Set<Minigame> loadedMinigames = new HashSet<Minigame>();

	public MinigameManager() {
		super(new File(SerialDataLoader.DATA_FOLDER + File.separator + "minigameData.json"));
	}

	/**
	 * Starts a minigame, an announces its joinability.
	 * 
	 * @param mg
	 *            the minigame to start
	 */
	public void startMinigame(Minigame mg) {
		mg.startAndAnnounce();
	}

	@Override
	protected void onLoad() {}
}
