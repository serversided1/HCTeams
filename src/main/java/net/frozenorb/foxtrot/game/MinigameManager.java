package net.frozenorb.foxtrot.game;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.frozenorb.foxtrot.command.CommandRegistrar;

import com.mongodb.BasicDBObject;

import lombok.Getter;

/**
 * Class that handles the creation and management of minigames.
 * 
 * @author Kerem Celik
 * 
 */
public class MinigameManager extends SerialDataLoader {

	public MinigameManager() {
		super(new File(SerialDataLoader.DATA_FOLDER + File.separator + "minigameData.json"));
		loadMinigames();
	}

	@Getter private Set<Minigame> loadedMinigames = new HashSet<Minigame>();
	@Getter protected Minigame currentMinigame;

	/**
	 * Loads all of the minigames from the package: <br>
	 * net.frozenorb.practice.game.games
	 */
	public void loadMinigames() {
		try {
			for (Class<?> cls : CommandRegistrar.getClassesInPackage("net.frozenorb.foxtrot.game.games_")) {

				Minigame minigame = (Minigame) cls.newInstance();
				if (data.containsField(minigame.getName())) {
					minigame.deserialize((BasicDBObject) data.get(minigame.getName()));
				}

				loadedMinigames.add(minigame);
			}
		}
		catch (InstantiationException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Gets the {@link Minigame} with the same given name or a similar alias.
	 * 
	 * @param name
	 *            the name to find
	 * @return minigame if found, null if not
	 */
	public Minigame findByName(String name) {
		name = name.replace('_', ' ');

		for (Minigame mg : loadedMinigames) {
			if (mg.getName().equalsIgnoreCase(name) || mg.getPrimaryCommandName().equalsIgnoreCase(name)) {
				return mg;
			}
			if (Arrays.asList(mg.getAliases()).contains(name.toLowerCase())) {
				return mg;
			}
		}
		return null;
	}

	/**
	 * Starts a minigame, an announces its joinability.
	 * 
	 * @param mg
	 *            the minigame to start
	 */
	public void startMinigame(Minigame mg) {
		this.currentMinigame = mg;
		mg.startAndAnnounce();
	}

	@Override
	protected void onLoad() {}
}
